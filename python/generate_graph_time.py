import os
import sys
import csv
import os.path
import matplotlib.pyplot as plt

def main():
    #leo el archivo
    #si esta en x>19.5  entonces llego a la salida
    #guardo su id y sugo iterando en todos
    step_index = 0
    cantZ = [2, 5,10, 15,20,25,30,35]
    # cantZ=[10]
    cant_index = 0
    cant2 =[]
    
    for cant in cantZ:
        ids = []
        time = []
        #leemos todos los archivos de un mismo timeline
        while step_index!= 10000:
            firstline = True
            csvfile ="../output/simulation_" + str(cantZ[cant_index])+"_1.0_"+ str(step_index)+ ".xyz"
            if os.path.isfile(csvfile):
                f = open(csvfile) 
                line = f.readline().split('\t')
            while os.path.isfile(csvfile) and line[0]!='':
                
                if firstline == False:
                    line = f.readline().split('\t')
                    if line[0]!='' and line[8] == "1.0\n" and float(line[2]) > 19 and int(line[0])>0: #es humano y esta  en la llegada
                        id = int(line[0])
                        if id in ids:
                            time[ids.index(id)] = (step_index/100)
                        else:
                            ids.append(id)
                            time.append(step_index/100)

                else:
                    f.readline()
                    firstline = False
            step_index+=50
        step_index = 0
        x = []
        y = []
        i=0
        print(time)
        while len(time)>0:
            t =time[0]
            i = time.count(t)
            print("cant of " + str(t) +" = "+ str(i))
            print(i)
            y.append(i)
            x.append(t)
            time.remove(t)
        
        # plt.subplots()
        
        
        plt.scatter(x, y, label='Class 2')
        cant_index+=1
    plt.savefig('time.png')



if __name__ == '__main__':
    main()
    plt.xlabel('tiempo (s)')
    plt.ylabel('Cantidad de humanos que llegan a la salida')
        
 