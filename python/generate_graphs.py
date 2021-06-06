import os
import sys
import csv
import os.path

def main():
    #leo el archivo
    #si esta en x>19.5  entonces llego a la salida
    #guardo su id y sugo iterando en todos
    step_index = 0
    cantZ = [2, 5,10, 15,20,25,30,35]
    cant_index = 0

    cant2 =[]
    for cant in cantZ:
        ids = []
        #leemos todos los archivos de un mismo timeline
        while step_index!= 10000:
            firstline = True
            csvfile ="../output/simulation_" + str(cantZ[cant_index])+"_1.0_"+ str(step_index)+ ".xyz"
            if os.path.isfile(csvfile):
                f = open(csvfile) 
                line = f.readline().split('\t')
            while os.path.isfile(csvfile) and line[0]!='':
                f.readline()
                if firstline == False:
                    line = f.readline().split('\t')
                    #print(line)
                    if line[0]!='' and line[8] == "1.0\n" and float(line[2]) > 18 and int(line[0])>0: #es humano y esta  en la llegada
                        
                        id = int(line[0])
                        ids.append(id)
                else:
                    firstline = False
            step_index+=50
            # print(step_index)
        step_index = 0
        res = []
        for i in ids:
            if i not in res:
                res.append(i)
        cant2.append(len(res))
        print(cant2)
        cant_index+=1



if __name__ == '__main__':
    main()

