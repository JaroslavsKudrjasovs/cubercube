**How to run the tests:**

**Create project in IntelliJ IDEA:**

On your local machine navigate to the folder where you want to create a maven project (for example C:\projects\)

Right click in the 'projects' folder and choose 'Git Clone'

In the 'URL' field enter 'https://github.com/JaroslavsKudrjasovs/cybercube_t1' and click 'OK'

Wait until remote repository is cloned to your 'projects' folder

Open Intellij IDEA

Choose 'File->New->Project from existing sources'

Enter the path to the cloned repository folder, i.e. 'projects'\cybercube_t1 (for example C:\projects\cybercube_t1) and click 'OK'

Choose 'Import project from external model', choose 'Maven' and click 'Finish'

If offered choose 'New Window'

Wait until Maven resolves dependencies (downloads all required libraries)


**Run the tests:**

Navigate to '.\src\test\resources\'

Right click 'smoke.xml' and choose 'Run' (Ctrl+Shift+F10)


**To view the results:**

Navigate to '.\test-output\old'
Right click 'index.html' and choose 'Open in->Browser->Chrome'
