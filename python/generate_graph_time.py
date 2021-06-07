import os
import sys
import csv
import os.path
import matplotlib.pyplot as plt
import numpy

xy1 = []
xy2 = []
xy3 = []
xy4 = []
xy5 = []
xy6 = []
xy7 = []
xy8 = []
def append_x_y(y,x, index):
    if index == 0:
        xy1.append([x,y])
    if index == 1:
        xy2.append([x,y])
    if index == 2:
        xy3.append([x,y])
    if index == 3:
        xy4.append([x,y])
    if index == 4:
        xy5.append([x,y])
    if index == 5:
        xy6.append([x,y])
    if index == 6:
        xy7.append([x,y])
    if index == 7:
        xy8.append([x,y])


def main():
    #leo el archivo
    #si esta en x>19.5  entonces llego a la salida
    #guardo su id y sugo iterando en todos
    step_index = 0
    cantZ = [2, 5,10, 15,20,25,30,35]
    cant_index = 0
    
    for cant in cantZ:
        ids = []
        time = []
        #leemos todos los archivos de un mismo timeline
        csvfile ="../output/simulation_b_" + str(cantZ[cant_index])+"_1.0_"+ str(step_index)+ ".xyz"
        while os.path.isfile(csvfile):
            firstline = True
            
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
            step_index+=10
            csvfile ="../output/simulation_b_" + str(cantZ[cant_index])+"_1.0_"+ str(step_index)+ ".xyz"
        step_index = 0
        x = []
        y = []
        i=0
        while len(time)>0:
            t =time[0]
            i = time.count(t)
            append_x_y(i, t, cant_index)
            time.remove(t)
        
        cant_index+=1
        
    

def sort(xy):
    aux_xy = []
    aux_xy.append([0,0])
    for e in xy:
        aux_xy.append(e)
    sorted_array = sorted(aux_xy, key=lambda tup: tup[0])
    for i in range(len(sorted_array)):
        if i!=0: 
            sorted_array[i][1]= sorted_array[i-1][1] + sorted_array[i][1]
    return sorted_array
    
    



if __name__ == '__main__':
    main()
    lab = [2, 5,10, 15,20,25,30,35]
    xy1 = sort(xy1)
    xy2 = sort(xy2)
    xy3 = sort(xy3)
    xy4 = sort(xy4)
    xy5 = sort(xy5)
    xy6 = sort(xy6)
    xy7 = sort(xy7)
    xy8 = sort(xy8)
    plt.plot([row[0] for row in xy1], [row[1] for row in xy1], label=lab[0])
    plt.plot([row[0] for row in xy2], [row[1] for row in xy2], label=lab[1])
    plt.plot([row[0] for row in xy3], [row[1] for row in xy3], label=lab[2])
    plt.plot([row[0] for row in xy4], [row[1] for row in xy4], label=lab[3])
    plt.plot([row[0] for row in xy5], [row[1] for row in xy5], label=lab[4])
    plt.plot([row[0] for row in xy6], [row[1] for row in xy6], label=lab[5])
    plt.plot([row[0] for row in xy7], [row[1] for row in xy7], label=lab[6])
    plt.plot([row[0] for row in xy8], [row[1] for row in xy8], label=lab[7])
    plt.xlabel('tiempo (s)')
    plt.ylabel('Porcentaje de humanos que llegan a la salida (%)')
    plt.ylim(0,100)
    plt.legend(loc='upper center', bbox_to_anchor=(0.65, 1.3),
          fancybox=True, shadow=True, ncol=4, title='Cantidad de zombies iniciales')
    plt.tight_layout()
    plt.savefig('time.png')
        

 