#This program is based on Markov Chains.
#Basic Logic: For each time point, find 20 historic prices, put them in a list.
#  Find the max and the min of those prices. Based on max and min, draw 3 lines to cut apart those 20 prices.
#  Those 3 lines has create 4 spaces. For each price, it must be contained in one of these spaces.
#  Each space is corresponding to a state. Now we have 4 state.
#  Build a 4x4 matrix.  Both x axis and y axis measure 4 states
#  Build a counter, count for each state, how many state-trasfer has happened.
#  For example, at t=0, price in state 2, and when it goes to t=1, price transfers to
#  state 4. Then for the matrix, the number at the second row and the forth column should +1
#  Then calculate the transfer frequency, then regard it as a transfer probability 
#  This matrix also called PTM
#  Find the initial State
#  Finally printout the current state matrix

import numpy as np

def initialize(context):
    context.stock = symbol('AAPL')

def handle_data(context, data):
    ApplyNum = 5
    TestNum = 20
    PriceMax = 0
    PriceMin = 0
    AllInfoList = []
    HisPriceList = []
    DistrictList = []
    
    #Find 20 history prices and put them in a list: ReducedInfoList
    AllInfoList.append(history(bar_count = TestNum, frequency = '1d', field = 'price', ffill=True))
    ReducedInfoList = str(AllInfoList).strip('Equity(24 [AAPL])').strip('[').strip(']').split()
    ReducedInfoListListLen = len(ReducedInfoList)
    
    PLen = ReducedInfoListListLen/3
    for i in range(1,PLen+1):
        HisPriceList.append(ReducedInfoList[3*i-1])
    PriceMin = float(HisPriceList[0])
    
    #Find the max and min of those 20 history prices
    for x in HisPriceList:
        if float(x) > PriceMax:
            PriceMax = float(x)
        elif float(x) < PriceMin:
            PriceMin = float(x)
            
    #Draw lines to split those prices
    MidLine = (PriceMax+PriceMin)/2
    FirstLine = (PriceMax+MidLine)/2
    ThirdLine = (PriceMin+MidLine)/2
    
    #Follow the above method, 3 lines have splited data into 4 different states
    for x in HisPriceList:
        if float(x) > FirstLine:
            DistrictList.append(0)
        elif float(x) > MidLine and float(x) < FirstLine:
            DistrictList.append(1)
        elif float(x) > ThirdLine and float(x) < MidLine:
            DistrictList.append(2)
        elif float(x) < ThirdLine:
            DistrictList.append(3)
            
    #Create a 4x4 matrix. Both x axis and y axis measure 4 states
    PTtable = [[0 for i in range(4)] for j in range(4)]
    
    #Counter, count for each state, how many state-trasfer has happened.
    #For example, at t=0, price in state 2, and when it goes to t=1, price transfers to
    #  state 4. Then for the matrix, the number at the second row and the forth column should +1
    for i in range(0,len(DistrictList)-1):
        PTtable[DistrictList[i]][DistrictList[i+1]]+=1
    TotalTrans = float(TestNum - 1)
    
    #Calculate the transfer frequency, then regard it as a transfer probability 
    # This matrix also called PTM
    for i in range(4):
        for j in range(4):
            PTtable[i][j] = round(PTtable[i][j]/TotalTrans,3)
    PTMatrix = np.array(PTtable)
    
    #Find the initial State
    InitialStateDistrict = DistrictList[-ApplyNum:]
    InitialStateTable = [0 for i in range(4)]
    for i in range(0,len(InitialStateTable)-1):
        InitialStateTable[InitialStateDistrict[i]]+=1
    InitialStateMatrix = np.array(InitialStateTable)
    
    #Returned How state changes
    print InitialStateMatrix * PTMatrix

    
    
