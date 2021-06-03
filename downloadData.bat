echo downloading the first dataset Superconductivty...
aria2c.exe http://www.maththinker.cn/datasets/Superconductivty.mat -d .\dist\in
echo downloading the second dataset MiningProcess...
aria2c.exe http://www.maththinker.cn/datasets/MiningProcess.mat -d .\dist\in
echo downloading the third dataset TripDuration...
aria2c.exe http://www.maththinker.cn/datasets/TripDuration.mat -d .\dist\in
echo Datasets download complete.
echo Copying the datasets...
copy .\dist\in\Superconductivty.mat .\in /Y
copy .\dist\in\MiningProcess.mat .\in /Y
copy .\dist\in\TripDuration.mat .\in /Y
echo Datasets copy complete.

