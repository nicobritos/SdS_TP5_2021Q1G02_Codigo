import os
import sys
import csv
import os.path

def main():
    #leo el archivo
    #si esta en x>19.5  entonces llego a la salida
    #guardo su id y sugo iterando en todos
    step_index = 0
    # cantZ = [2, 5,10, 15,20,25,30,35]
    cantZ=[10]
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
            # print(csvfile)
            while os.path.isfile(csvfile) and line[0]!='':
                
                if firstline == False:
                    line = f.readline().split('\t')

                    # if line[0]!= '' and (step_index == 1100):
                    #     print(line)
                    #     print(step_index)
                    #     print(line[0] + ' ' + line[2] )
                    if line[0]!='' and line[8] == "1.0\n" and float(line[2]) > 19 and int(line[0])>0: #es humano y esta  en la llegada
                        print(step_index)
                        id = int(line[0])
                        ids.append(id)
                else:
                    f.readline()
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
        # print(ids)
        cant_index+=1



if __name__ == '__main__':
    main()

