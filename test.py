# step1. import package
import requests
import pandas as pd
import numpy as np
from io import StringIO

# step2. 進入目標網站,爬取盤後資訊
date = '20180606'
r = requests.post('http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=' + date + '&type=ALL')

# step3. 篩選出個股盤後資訊
str_list = []
for i in r.text.split('\n'):
    if len(i.split('",')) == 17 and i[0] != '=':
        i = i.strip(",\r\n")
        str_list.append(i)

# step4. 印出選股資訊
df = pd.read_csv(StringIO("\n".join(str_list)))
pd.set_option('display.max_rows', None)
df.head(150)