#Regression model for WeatherEye using NARX-MLP

import time
import scipy.io as sio
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.preprocessing import StandardScaler
from keras.models import Sequential
from keras.layers.core import Dense
import tensorflow as tf
from matplotlib import pyplot as plt
import joblib
import pandas as pd

# parameters
tf.compat.v1.disable_eager_execution()
useScaling = 0
useNormalization = 0
learningRate = 1e-3
weightDecay = 1e-3 / 100
maxEpochs = 10000;
miniBatchSize = 512
lag = 5
# hidden layer sizes
h1 = 30
h2 = 30
h3 = 1

# One step ahead 1 to 12 hours
osa = 2

dataframe1 = pd.read_csv('train_data/train_data'+str(osa)+'.csv')
uall = dataframe1[['u1','u2']];
uall = np.float32(uall)

yall = dataframe1[['y1']];
yall = np.float32(yall)
 
print(dataframe1)

p,q = uall.shape

mm_scaler = MinMaxScaler(feature_range=(0, 1), copy=True)
uall_scaled = mm_scaler.fit_transform(uall);
print(uall)

scaler_filename = "scaler.save"
joblib.dump(mm_scaler, scaler_filename) 

#uall_scaled = uall
yall_scaled = yall;

trnsize = round(p/2);
tstsize = round(p/2);
lagx = 0

# psi_trn
psi_trn = uall_scaled[lag:trnsize-1]
lag2 = lag-1
for it in range(lag2):
    takenlagstart = lag2-it
    takenlagend = trnsize-(it+2)
    ut_trn = uall_scaled[takenlagstart:takenlagend]
    psi_trn = np.concatenate((ut_trn, psi_trn), 1)

    
for it in range(lag):
    takenlagstart = lag-it
    takenlagend = trnsize-(it+1)
    yt_trn = yall_scaled[takenlagstart:takenlagend]
    psi_trn = np.concatenate((yt_trn, psi_trn), 1)
    
    
# psi_tst
psi_tst = uall_scaled[(tstsize+lag-1):(p-1)]

lag2 = lag-1
for it in range(lag2):
    takenlagstart = tstsize+lag2-it-1
    takenlagend = p-it-2
    ut_tst = uall_scaled[takenlagstart:takenlagend]
    psi_tst = np.concatenate((ut_tst, psi_tst), 1)
    
for it in range(lag):
    takenlagstart = tstsize+lag2-it
    takenlagend = p-(it+1)
    yt_tst = yall_scaled[takenlagstart:takenlagend]
    psi_tst = np.concatenate((yt_tst, psi_tst), 1)
    
    
yt_trn = yall_scaled[(lag+1):round(p/2)]
yt_tst = yall_scaled[(round(p/2)+lag):p]


###############################################################################
# define and create the MLP network
model = Sequential()
model.add(Dense(h1, input_dim=len(psi_trn[0]), activation="relu"))
model.add(Dense(h2, activation="relu"))
model.add(Dense(h3, activation="linear"))

# compile the model
opt = tf.optimizers.Adam(lr = learningRate, decay = weightDecay)
model.compile(loss="mean_absolute_percentage_error", optimizer=opt)
 
# train the model
print(psi_trn[0])
time.sleep(10)
print("[INFO] training model...")
ccc=model.fit(psi_trn, yt_trn, validation_data=(psi_tst, yt_tst), epochs=maxEpochs, batch_size=miniBatchSize)

# make predictions on the training data
print("Predicting Training Set...")
preds_trn = model.predict(psi_trn)
yhat_trn = preds_trn.flatten()
resid_trn = yt_trn.flatten() - yhat_trn
percentDiff_trn = (resid_trn / yt_trn) * 100
absPercentDiff_trn = np.abs(percentDiff_trn)
mean_trn = np.mean(absPercentDiff_trn)
std_trn = np.std(absPercentDiff_trn)
plt.figure()
jj,kk = yt_trn.shape
plt.plot(np.arange(1,jj+1), yt_trn)
plt.plot (np.arange(1,jj+1), yhat_trn, ':')
plt.xlabel("Cases (dimensionless)")
plt.ylabel("Raindrop (R)")
plt.title("One Step Ahead Prediction (Training Set)")
# plt.ylim([0, 100])
plt.show()

# make predictions on the testing data
print("Predicting Testing Set...")
preds_tst = model.predict(psi_tst)
yhat_tst = preds_tst.flatten()
resid_tst = yt_tst.flatten() - yhat_tst
percentDiff_tst = (resid_tst / yt_tst) * 100
absPercentDiff_tst = np.abs(percentDiff_tst)
mean_tst = np.mean(absPercentDiff_tst)
std_tst = np.std(absPercentDiff_tst)
plt.figure()
jj,kk = yt_tst.shape
plt.plot(np.arange(1,jj+1), yt_tst)
plt.plot (np.arange(1,jj+1), yhat_tst, ':')
plt.xlabel("Cases (dimensionless)")
plt.ylabel("Raindrop (R)")
plt.title("One Step Ahead Prediction (Testing Set)")
plt.legend(['Actual', 'Forecasted']);
# plt.ylim([0, 100])
plt.show()

# plot the residuals 
plt.figure()
plt.subplot(211)
jj,kk = psi_trn.shape
plt.plot(np.arange(1,jj+1), resid_trn)
plt.xlabel("Cases (dimensionless)")
plt.ylabel("Angular Velocity Difference (w)")
plt.title("Residuals Plot (Training & Testing Set)")
plt.subplot(212)
jj,kk = psi_tst.shape
plt.plot(np.arange(1,jj+1), resid_tst)
plt.xlabel("Cases (dimensionless)")
plt.ylabel("Angular Velocity Difference (w)")
# plt.title("Residuals Plot (Testing Set)")
plt.show()

# Output the predictions result in cv and json format
TempResult = pd.DataFrame()
TempResult['actual'] = yt_tst.tolist()
TempResult['prediction'] = preds_tst.tolist()
TempResult.to_csv("prediction.csv")
TempResult.to_json("prediction.json")

model.save("net\model"+str(osa)+".h5")