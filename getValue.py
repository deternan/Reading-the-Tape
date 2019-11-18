# coding=utf8

import requests
import pandas as pd
import numpy as np
from io import StringIO

date = '20191118'
r = requests.post('http://www.twse.com.tw/exchangeReport/MI_INDEX?response=html&date=' + date + '&type=ALL')
#r = requests.post('http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=' + date + '&type=ALL')

str_list = []
for i in r.text.split('\n'):
    if len(i.split('",')) == 17 :
        i = i.strip(",\r\n")
        str_list.append(i)
        print(i)
#    if len(i.split('",')) == 16 and len(i[0]) == 4:
#    if len(i.split('",')) == 17 and i[0] != '=':

print(str_list)

# df = pd.read_csv(StringIO("\n".join(str_list)))
# pd.set_option('display.max_rows', None)
# df.head(150)

print('end')