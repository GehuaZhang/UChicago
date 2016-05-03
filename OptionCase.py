# -*- coding: utf-8 -*


#Basic Logic:
# First simulate Brownian Motion, then simulate Geometric Brownian Motion, then find the broker's price.
# Based on Brokers' price, decide how much volume we should trade.

import random
import math
import numpy as np
from scipy.stats import norm

class Quote:
    
    def BrownianMotion(self,x0,n): 
        #Generate Brownian Motion from: W(t1)-W(t2) ~ N(0,t2-t1)
        #Brownian increments are normally distributed
        result = x0
        for i in range(n):
            result = result + np.random.normal(0,1)
        return result
        
    def GBM(self,S0,volatility):
        #Generate Geometric Brownian Motion from Brownian Motion
        St = float(S0)*math.exp(float(volatility)*self.BrownianMotion(0,1))
        return St
    
    def QuoteList(self):
        #Generate strike price from 80,90,100,110,120 randomly
        Strike = random.randrange(80,121,10) #(start,end,step size)
        return Strike
    
    def vega(self,Strike,sigma):
        #vega is calculated based on S, T and sigma
        strike = float(Strike)
        dPositive = (math.log(100.0/strike)+(0.01 + 0.5*sigma**2))/sigma
        vega = 100/math.sqrt(2*math.pi)*math.exp(-dPositive**2/2)
        return vega
        
    def vegaLimit(self):
        return 5*self.vega(100,0.3)
    
    def getCurrentQuote(self,Strike):
        #Generate sigma from Geometric Brownian Motion
        sigma = self.GBM(0.3,0.05)        
        #Randomly pick a strike price from quote list
        strike = float(Strike)        
        #Calculate Black-Scholes formula
        dPositive = (math.log(100.0/strike)+(0.01 + 0.5*sigma**2))/sigma
        dNegative = (math.log(100.0/strike)+(0.01 - 0.5*sigma**2))/sigma
        EuroCall = 100.0*norm.cdf(dPositive) - strike*math.exp(-0.01)*norm.cdf(dNegative)        
        #Et is the noise parameter        
        Et = np.random.normal(1,0.05)        
        #Generate the direction randomly 
        randomNum = random.uniform(0,1)
        if randomNum < 0.5:
            direction = 1
        elif randomNum >= 0.5:
            direction = -1
        else:
            direction = 0            
        #Consider the 5% edge that broker is willing to pay
        if direction == 1:
            et = 1.05
        elif direction == -1:
            et = 0.95
        else:
            et = 0      
        #Final quote price
        Price = EuroCall*Et*et
        return [Price,strike,direction]
        
    def Main(self,BrokerNum,quantile):
        #BrokerNum is 100 per round
        #Quantile is chosen manually, used to determine how much proportion will be traded.
        bidList = []
        offerList = []
        m,j = 0,0 #Counter
        PnL = 0.0
        for i in range(BrokerNum):
            strike = self.QuoteList()
            info = self.getCurrentQuote(strike)
            #split bid and offer orders
            if info[2] == 1:
                bidList.append(info[0])
                m=m+1
            elif info[2] == -1:
                offerList.append(info[0])
                j=j+1
        #Find an appropriate trading volume
        dealNum = int(min(float(j)*quantile,float(m)*quantile))
        
        sortedBidList = sorted(bidList,reverse=True)
        sortedOfferList = sorted(offerList)
        
        for i in range(dealNum):
            PnL+= sortedBidList[i] - sortedOfferList[i]
        #print "Strike Price: "+ str(strike)
        print "Quantile: " + str(quantile)
        print "Trade amount: " + str(dealNum)
        print "Total Profit: "+ str(PnL)

print Quote().Main(100,0.7)
