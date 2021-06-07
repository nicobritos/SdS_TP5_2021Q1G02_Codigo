import os
import sys
import csv
import os.path

def main():
    step_index = 0
    cantZ = [2, 5,10, 15,20,25,30,35]
    cant_index = 0

    cant2 =[]
    for cant in cantZ:
        ids = []
        #leemos todos los archivos de un mismo timeline
        csvfile ="../output/simulation_b_" + str(cantZ[cant_index])+"_1.0_"+ str(step_index)+ ".xyz"
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
            step_index+=50
            csvfile ="../output/simulation_b_" + str(cantZ[cant_index])+"_1.0_"+ str(step_index)+ ".xyz"
        step_index = 0
        cant2.append(len(ids))
        cant_index+=1
    print(cant2)



if __name__ == '__main__':
    main()

