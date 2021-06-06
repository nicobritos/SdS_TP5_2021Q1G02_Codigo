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
    cantZ = [2, 5,10, 15,20,25,30,35]
    csvfile ="info.csv"
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
    x = [2, 5,10, 15,20,25,30,35]
    y = data
    print(std_error)
    #create scatter chart with error bars
    
    plt.subplots()
    
    plt.xlabel('Cantidad de zombies')
    plt.ylabel('Cantidad de humanos que llegan a la salida')

    plt.errorbar(x, y, yerr=std_error, fmt='o')
    plt.savefig('alive.png')





if __name__ == '__main__':
    main()
