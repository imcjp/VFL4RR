
## Code for Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution

The project is the code for the paper "Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution".

In this project, we designed a simulation platform for federated learning and implemented our algorithm based on this platform.

### The software environment of the source code requires Java 8, eclipse and MATLAB 2017.

### Note: in order to run this project successfully, Java 8 and Matlab 2017 must be installed correctly!!! Besides, your computer should have enough memory (such as 16G) to hold the data, otherwise it may not be able to load the largest data set “TripDuration”.



#### Instructions:

1. Our data set is too large to upload to GitHub. So the content you download contains only code, not data sets. Please run "downloadData.bat" after download the code. It will automatically download the three data sets in the format of .mat from our website.
2. We provide all the source code of our algorithms and the released jar package. If you need to run our algorithms directly, you can cd to the folder "dist" through the console. Then execute the commands as follows.

Command line to run our algorithms:

For VRG:
java -jar VFL4RRExp.jar "{'lambda':10.0,'data':{'fileName':'Superconductivty.mat','X':'XData_norm','Y':'yData','learningRate':1.0,'initBias':10.0},'parties':{'partyNum':2,'attrBlockNum':2},'iteration':10,'subDataNum':1000,'sampleInfo':{'sampleBlockNum':10,'testBlockId':1},'algorithm':'VRG'}"

For VRL-2P:
java -jar VFL4RRExp.jar "{'lambda':10.0,'data':{'fileName':'Superconductivty.mat','X':'XData_norm','Y':'yData'},'parties':{'partyNum':2,'attrBlockNum':2},'subDataNum':10000,'sampleInfo':{'sampleBlockNum':10,'testBlockId':1},'algorithm':'VRL_2P'}"

For VRL-MP:
java -jar VFL4RRExp.jar "{'lambda':10.0,'data':{'fileName':'Superconductivty.mat','X':'XData_norm','Y':'yData'},'parties':{'partyNum':5,'attrBlockNum':5},'subDataNum':10000,'sampleInfo':{'sampleBlockNum':10,'testBlockId':1},'algorithm':'VRL_MP'}"

#### The command line parameters are explained as follows:

**algorithm: **the algorithm used in your experiment. The optional parameters are VRG, VRL_ 2p and VRL_ MP。

**lambda: **lambda parameter of ridge regression.

**data: ** information about the dataset. The "fileName" is the name of the file of the dataset in the folder "in" or "dist\in"; "X" is the feature data in the dataset; :Y" is the numerical label in the dataset. The "learningRate" is the learning rate of VRG algorithm; The "initBias" is the size of the initial random model parameter w0 of VRG algorithm.

**parties: **information about federal learning participants. The "attrBlockNum" means that the features of data set are divided into "attrBlockNum" blocks averagely; the "partyNum" is the number of participants.

**iteration: **if VRG algorithm cannot find better model parameters for successive "iteration" times, the algorithm stops.

**subDataNum: **indicates the size of the sub data set used in the experiment. If the subDataNum= - 1, it means that the complete dataset is used for the experiment.

**sampleInfo: **our experiment uses k-fold cross validation. The "sampleBlockNum" means that the dataset is divided into "sampleBlockNum" blocks averagely, "testBlockId" means that the "testBlockId"-th block is the test set, and the rest is the training set. If testBlockId= 0, the experiment does not use test set.

