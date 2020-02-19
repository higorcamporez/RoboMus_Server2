import keras
import sys
import numpy as np
from keras.models import load_model
import keras.losses
from keras import backend as K
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
import random
import csv

def getMaxValues(name):
    file = open(name+"_max_values.txt", "r") 
    max_x = file.readline()
    max_y = file.readline()
    return float(max_x),float(max_y)
        
def root_mean_squared_error(y_true, y_pred):
        return K.sqrt(K.mean(K.square(y_pred - y_true), axis=-1))

name = "BongoBot"
max_x,max_y = getMaxValues(name)

# load model
model = load_model(name+'_keras_model.h5',
                   custom_objects={'root_mean_squared_error': root_mean_squared_error})

f = open(name+'_msgs.csv','w',newline='')
writer = csv.writer(f)
pwm_ant = 500
for i in range(1000):
    pwm = random.randint(500, 1024)
    pwm_norm = pwm/max_x
    
    inp = np.array([[0, pwm_ant/max_x, 0, pwm_norm]])
    #print(pwm,model.predict(inp)[0][0]*max_y)
    writer.writerow((pwm,model.predict(inp)[0][0]*max_y))
    pwm_ant = pwm
f.close()
