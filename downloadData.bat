echo downloading the first dataset Superconductivty...
aria2c.exe http://www.maththinker.cn/datasets/Superconductivty.mat -d .\release\in
echo downloading the second dataset MiningProcess...
aria2c.exe http://www.maththinker.cn/datasets/MiningProcess.mat -d .\release\in
echo downloading the third dataset TripDuration...
aria2c.exe http://www.maththinker.cn/datasets/TripDuration.mat -d .\release\in
echo Datasets download complete.
echo Copying the datasets...
copy .\release\in\Superconductivty.mat .\VFL4RR\in /Y
copy .\release\in\MiningProcess.mat .\VFL4RR\in /Y
copy .\release\in\TripDuration.mat .\VFL4RR\in /Y
echo Datasets copy complete.

