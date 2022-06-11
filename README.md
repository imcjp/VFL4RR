
## Code for Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution

The project is the code for the paper **"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"**.

In this project, we designed a simulation platform for federated learning and implemented our algorithm based on this platform.

The software environment of the source code requires **Java 1.8.0**, **eclipse** and **MATLAB 2017**.

**Note:** in order to run this project successfully, Java 1.8 and Matlab 2017 must be installed correctly!!! Besides, your computer should have enough memory (such as 32G) to hold the data, otherwise it may not be able to load the largest data set “TripDuration”.



#### Instructions:

1. Our datasets are too large to upload to GitHub. So the content you download contains only code, not data sets. Please run **"downloadData.bat"** to download our datasets after download the code. It will automatically download the three data sets in the format of .mat from our website.

2. We provide all the source code of our algorithms and the released jar package. The source code is in the folder **VFL4RR**, implemented in Eclipse.

3. We provide the compiled .jar file in the folder **release**. If you need to run our algorithms directly, you can cd to the folder **release** through the console. Then execute the commands as follows. Some command line to run our algorithms as following:

    For NoFed:
    java -Xms14576m -Xmx14576m -Xmn8480m -jar VFL4RRExp.jar "{'algorithm':'NoFed','data': {'fileName': 'Superconductivty.mat','X': 'XData_norm','Y': 'yData'},'lambda':10,'parties': {'partyNum':1,'attrBlockNum':1},'subAttrNum':-1,'sampleInfo': {'sampleBlockNum':5,'testBlockId':1},'subDataNum':1000}"
    
    For VRG:
    java -Xms14576m -Xmx14576m -Xmn8480m -jar VFL4RRExp.jar "{'algorithm':'VRG','data': {'fileName': 'Superconductivty.mat','X': 'XData_norm','Y': 'yData','learningRate': 1,'initBias': 1},'lambda':10,'parties': {'partyNum':2,'attrBlockNum':2},'subAttrNum':-1,'sampleInfo': {'sampleBlockNum':5,'testBlockId':1},'subDataNum':1000,'iteration':10}"
    
    For VRL-2P:
    java  -Xms14576m -Xmx14576m -Xmn8480m -jar VFL4RRExp.jar "{'algorithm':'VRL_2P','data': {'fileName': 'Superconductivty.mat','X': 'XData_norm','Y': 'yData'},'lambda':10,'parties': {'partyNum':2,'attrBlockNum':2},'subAttrNum':-1,'sampleInfo': {'sampleBlockNum':5,'testBlockId':1},'subDataNum':1000}"
    
    For VRL-MP:
    java  -Xms14576m -Xmx14576m -Xmn8480m -jar VFL4RRExp.jar "{'algorithm':'VRL_MP','data': {'fileName': 'Superconductivty.mat','X': 'XData_norm','Y': 'yData'},'lambda':10,'parties': {'partyNum':5,'attrBlockNum':5},'subAttrNum':-1,'sampleInfo': {'sampleBlockNum':5,'testBlockId':1},'subDataNum':1000}"

4. More information about parameter settings please use the Excel file **ParamSettingHelper.xlsx**. To help users quickly use our software, we have developed the Excel tool. Users can visually set parameters within this tool and it will automatically generate the corresponding command line. We provide careful support for users. You don't need to worry about setting the wrong parameters, because **ParamSettingHelper.xlsx** will automatically indicate that the user has entered the wrong parameters.

5. To ensure the correctness of the performance analysis in the paper, we provide the automated theoretical derivation in the folder **theoreticalDerivationOfPerformanceAnalysis**. Please enter the folder for details.

If you have any questions or suggestions for improvement, please contact email jpingcai@163.com. Thank you!
