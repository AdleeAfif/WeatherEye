import json
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.preprocessing import StandardScaler
from keras.models import load_model
from datetime import datetime
import pandas as pd
import time
from pandas import json_normalize
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import requests

lag = 5
numregressor = 3

# Fetch the service account key JSON file contents    
cred = credentials.Certificate('./weathereye-a1f30-firebase-adminsdk-nfj1m-20f10a9c00.json')

# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://weathereye-a1f30-default-rtdb.firebaseio.com/'
})


# Function to save raindrop prediction to WeatherEye Firebase
def sendFirebase():
    try:
        # api-endpoint
        response_API = requests.get('https://api.thingspeak.com/channels/1807868/fields/1&2&4.json?api_key=1NZVJLNFHRAE5EP4&results=5&timezone=Asia/Kuala_Lumpur')
        data = response_API.text
        parse_json = json.loads(data)

        # Change the fetched data to dataframe then to numpy float format
        df = json_normalize(parse_json['feeds']) 
        df_subset = df[['field1', 'field2', 'field4']]
        df_subset = np.float32(df_subset)

        pred = []

        for osa in range(1,13):
            model = load_model("net/model"+str(osa)+".h5")
            
            # Transform the fetched data to fit into model
            psi = df_subset
            psi = psi.reshape((-1, 1))  
            data = psi.T 
            
            # Store the forecast in single_pred
            single_pred = int(model.predict(data))
            
            if(single_pred > 100):
                single_pred = 100
            elif(single_pred < 0):
                single_pred = single_pred * - 1
                
            # Store the forecasts in pred array
            pred = np.append(pred,single_pred )
            # print(single_pred)
            
        
        print(pred)
        
        # Change the predictions data into dataframe format
        dataframeDB = pd.DataFrame(pred, columns=['Next 12 Hours'])

        # Change to dict format
        dataDB = dataframeDB.to_dict()

        # Specify the child node to write data to
        child_node = 'Forecast'

        # Write data to the child node
        db.reference(child_node).set(dataDB)
        print('Forecast is successfully sent to Firebase!')
        
    except:
        print('Failed to send to Firebase')

while 1:
    sendFirebase()
    time.sleep(300)
        
        

