import matplotlib.pyplot as plt
import numpy as np
import os.path
import statistics
import csv

def line_to_int(line):
    aux =[]
    for e in line:
        aux.append(int(e))
    return aux
data = []
std_error = []
def main():
    cantZ = [0.4, 0.8, 1.2, 1.6, 2, 2.4]
    csvfile ="info2.csv"
    if os.path.isfile(csvfile):
        f = open(csvfile) 
        f.readline()  
    for cant in cantZ :
        line= f.readline().split(';')  
        arr = line_to_int(line)
        media = statistics.mean(arr)
        desvEstandar = statistics.stdev(arr)
        data.append(media)
        std_error.append(desvEstandar)


    #define x and y coordinates
    x = [0.4, 0.8, 1.2, 1.6, 2, 2.4]
    y = data
    print(std_error)
    #create scatter chart with error bars
    
    plt.subplots()
    
    plt.xlabel('Velocidad de zombies (m/s)')
    plt.ylabel('Fraccion de humanos que llegan a la salida')

    plt.errorbar(x, y, yerr=std_error, fmt='o')
    plt.savefig('alive_velocity.png')





if __name__ == '__main__':
    main()
