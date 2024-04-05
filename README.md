<h3>How to run the tests:</h3>

<h4>Create project in IntelliJ IDEA:</h4>
1) On your local machine navigate to the folder where you want to create a maven project (for example C:\projects\)
2) Right click in the 'projects' folder and choose 'Git Clone'
3) In the 'URL' field enter 'https://github.com/JaroslavsKudrjasovs/cybercube_t1' and click 'OK'
4) Wait until remote repository is cloned to your 'projects' folder
5) Open Intellij IDEA
6) Choose 'File->New->Project from existing sources'
7) Enter the path to the cloned repository folder, i.e. 'projects'\cybercube_t1 (for example C:\projects\cybercube_t1) and click 'OK'
8) Choose 'Import project from external model', choose 'Maven' and click 'Finish'
9) If offered choose 'New Window'
10) Wait until Maven resolves dependencies (downloads all required libraries)

<h4>Run the tests:</h4>
1. Navigate to '.\src\test\suites\'
2. Right click 'smoke.xml' and choose 'Run' (Ctrl+Shift+F10)

<h4>To view the results:</h4>
1. Navigate to '.\test-output\html'
2. Right click 'index.html' and choose 'Open in->Browser->Chrome'
