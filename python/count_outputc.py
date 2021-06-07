import os
import sys
import csv
import os.path

def main():
    #leo el archivo
    #si esta en x>19.5  entonces llego a la salida
    #guardo su id y sugo iterando en todos
    step_index = 0
    cantZ = [0.4, 0.8, 1.2, 1.6, 2, 2.4]
    # cantZ=[10]
    cant_index = 0

    cant2 =[]
    for cant in cantZ:
        ids = []
        #leemos todos los archivos de un mismo timeline
        csvfile ="../output/simulation_c_10_" + str(cantZ[cant_index])+"_"+ str(step_index)+ ".xyz"
        while os.path.isfile(csvfile):
            firstline = True
            
            if os.path.isfile(csvfile):
                f = open(csvfile) 
                line = f.readline().split('\t')
            # print(csvfile)
            while os.path.isfile(csvfile) and line[0]!='':
                
                if firstline == False:
                    line = f.readline().split('\t')
                    if line[0]!='' and line[8] == "1.0\n" and float(line[2]) > 19 and float(line[3]) > 8 and float(line[3]) < 12 and int(line[0])>0: #es humano y esta  en la llegada
                        id = int(line[0])
                        if id not in ids:
                            ids.append(id)
                else:
                    f.readline()
                    firstline = False
            step_index+=10
            csvfile ="../output/simulation_c_10_" + str(cantZ[cant_index])+"_"+ str(step_index)+ ".xyz"
        step_index = 0
        cant2.append(len(ids))
        cant_index+=1
    print(cant2)



if __name__ == '__main__':
    main()

