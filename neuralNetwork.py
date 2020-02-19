import keras
import sys
import numpy as np
from keras.models import load_model
import keras.losses
from keras import backend as K
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'


def getMaxValues(name):
    file = open(name+"_max_values.txt", "r") 
    max_x = file.readline()
    max_y = file.readline()
    return float(max_x),float(max_y)
        
def root_mean_squared_error(y_true, y_pred):
        return K.sqrt(K.mean(K.square(y_pred - y_true), axis=-1))

if(len(sys.argv)<6):
    print("-1")
    exit()
    
name = sys.argv[1]
max_x,max_y = getMaxValues(name)

# load model
model = load_model(name+'_keras_model.h5',
                   custom_objects={'root_mean_squared_error': root_mean_squared_error})

#model.summary()
inp = np.array([[float(sys.argv[2]), float(sys.argv[3]),
                 float(sys.argv[4]), float(sys.argv[5])]])
inp = inp/max_x #normalização
print(model.predict(inp)[0][0]*max_y)

