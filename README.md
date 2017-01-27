#Ebola Worker
Welcome to the Ebola Worker,
A Machine-Learning Prediction Tool for the Triage and Clinical Management of Ebola Virus disease.

#Intro
When suspect patients come to the treatment centers they first go through the so-called “triage” wards. In these selection areas, they are then redistributed into several low- to high-risk quarantines. The patients will stay there until lab test result is available proving them EVD(+) or EVD (-). The selection (triage) of “high-risk” patients is very difficult because of the non-specific symptoms of the Ebola Virus Disease (EVD). What needs to be limited is to put an EVD(+) patient in a quarantine with a EVD(-) one. 
	
#Background
In Ebola treatment centers, nosocomial infection risk is compartmentalized by triaging suspect Ebola patients into various risk categories while they await confirmatory test results. The identification of “high-risk” patients is very difficult because of the non-specific symptoms of the Ebola Virus Disease (EVD). During the 2014 Ebola outbreak in West Africa, this process was shown to be little better than flipping a coin, with just over 50% accuracy [1]. Additionally, the rapid evolution of the virus during the outbreak changed the symptomatic presentation over time, which further degraded the accuracy of static case definitions. 
The Ebola Worker application is a user-friendly prediction tool, which rapidly calculates infection risk using statistically weighted patient symptoms and demographic information. Its machine learning (ML) capabilities allow it to learn from its environment, thus providing a score that adapts to the evolving virus and patient characteristics.

The score is based on the system described by Hartley et al. [2], which discriminates EVD(+) from EVD(-) with a 89% accuracy. Each additional patient entered will be used to improve this predictive power and adapt it to the local population.
Ebola Worker can also be used as a patient database. It collects comprehensive clinical information at triage, and is capable of reading patient bar codes and storing demographic data, which can then be exported to the health information system.
All data is then anonymized for further epidemiological analysis to improve our clinical understanding of this devastating disease.

#Use
When opening the app the very first time, the **settings** will open. Here the user is invited to enter his or her personal information (Name, email, location). (This feature is still under development; see future features). Once validated, the home screen is visible.

From the home screen we have the following options:
-	New Patient
-	Search Patient
-	Settings (gear icon, bottom left)
-	Update (update icon, bottom right)

Adding a **new patient** opens the triage activity. Triage contains three tabs (patient information, symptoms and result).

*Patient information*: Here the information about the patient can be entered. The app will automatically save the date of the triage and create a patient id that is unique to the app. This id can be used to search for patients later on. Alternatively a barcode can be scanned if an id for the patient already exists. Furthermore there is the possibility to add a picture, input the gender, birthdate, address and more.

*Symptoms*: This screen can be access from the patient information by either clicking on the tab or by swiping left. 15 symptoms with 3 possible values (Yes, No, Unknown) are represented to the user. After entering all the values that are known, the date of the first symptom can be entered. By default this is set to the triage date, it is very important though that here the right date is entered since it has an influence on the prediction score. Before passing on to the last screen to see the result you need to enter the value of each symptom. This can either be done manually or with a button (set unknown) that sets all symptoms without value to Unknown. More features for this screen are planned for the future (see future features).

*Result*: Here the user gets to see the Ebola infection risk score that has been calculated based on the information and symptoms he or she put in. The score is presented as a value and as a cross inside a bar, to help interpret the result. The bar has 5 classes, from very low (dark green) to very high (dark red). Under the bar there is a new 3-value input field that lets the user input the actual test result of the patient once it is known.  This is very important as we only can improve you prediction score if we know if the patient was confirmed EVD(+) or EVD(-).
If the user put in EVD(+) the Ebola severity score becomes visible. This score is based on the system described by Hartley et al. [3]. The Ebola severity score is still under development (see future features).

**Searching** for a patient is very easy. You can search by: First Name, Last Name, Birthdate and OCI code. You can enter several information at the same time (e.g. first name + birthdate).
If only one patient was found, you will be redirected to his triage screen directly. If several patients match your criteria you will see a list of all possible matches, clicking on one will charge its triage screen.

The **settings** icon will lead back to the settings as described above.

The **update** icon can have a little red note, telling the user that a new update is available. Clicking on it will lead to the following:
-	If a new prediction model is available it is being downloaded and installed on the phone. This will happen instantly in the background and the user will take no notice of it. If we propose a new prediction model to you it means that we improved our prediction score based on new information we obtained by you and other users around you (location based).
-	All the data you collected will be made anonyms and send to our headquarters in Lausanne, Switzerland.
Doing updates is very important and helps not only you, by giving you a better prediction but also us, by giving us access to your anonymized data. For a more detailed explanation refer to the ML part.

#Future Features
 This app has been implemented as a student semester project and should not be seen as a final version for now. The basic functions are present (triage with prediction risk score, database usage) but there are a few important features missing:
-	Severity score: Using statistically weighted symptoms and demographic characteristics, we provide a score discriminate death at or after triage respectively as described by Hartley et al. [3].
-	Settings: Permitting the user enter his or her information such as name, email and location. This is important since we need this to verify the data we receive from each user.
-	Updates: Our apps’ ML capabilities will permit it to learn from the provided data, thus providing a score that adapts to the evolving virus and patient characteristics. Clicking on the update button will send us you anonymized data with which we will then improve our predictions. This is a both way improvement and so sharing the data is also interesting for the user.
 Additionally to that we have several ideas on how the app could be improved by adding new or fixing current features:
-	Giving the user the possibility to add a new symptom to the list
-	Entering a custom value for a symptom (e.g. the measured temperature for fever)
-	Reducing the size of the barcode scanner, so scanning will become faster.
-	Changing the orientation of the barcode scanner (from vertical to horizontal), so the user does not need to turn the device (since the barcodes on papers are usually horizontally aligned)
-	Saving the picture to the database (it will not be included in the data sent to us)

#Improving the prediction with Machine Learning
In addition to providing a user-friendly prediction tool and a modern patient database we also want to give you the best predictions based on your location and past patients. The rapid evolution of the virus during the outbreak changed the symptomatic presentation over time, which also made it clear to us that we need to adapt to those changes with our prediction score. The goal of the ML feature is that with the data provided by users we can adapt to the evolution of the virus and also change the prediction. Furthermore by taking into account the location of the patients we can also adapt to local evolutions.

Once the user presses on that update button the local database will be anonymized (removing names, pictures and any other private information that is not relevant for the prediction). Only gender, birth date, triage date, date of first symptoms, location and the symptoms will be saved. It will then be send to our headquarters in Lausanne, Switzerland. 
We will then go over your data to make sure it is complete and useable for our purpose.
Once enough data (per location) is assembled we can run our ML script (more details on that below). If we found a better prediction for the data we have access to, we will then publish the updated version on our servers and the user will see a little red note in their app (above the update icon), telling them that a new prediction model is available. 

To predict the risk of a suspect patient we will use a classification technique called Logistic Regression. In our case the classification consists of taking a labeled dataset of patients (labeled = we know which data point is which patient and we know which dimension of the data point is which symptom) and creating a model that best represents this dataset. If we then give to this model a new input (patient), it can tell us the output value (EVD result) that would correspond to its model given the input. We used Scikit-learn (a machine learning python library) to create a script that automates what is described above.
  
The script follows several steps:  

1. We will load the dataset and select the symptoms we will use to create the model. One symptom will represent one dimension for one data point (patient). These are our input values (X). We then link the input to the output value y (EVD result) of that patient.

2. Logistic Regression has several hyper-parameters that let us customize the algorithm; one of those is the class weight.  We have two different output values EVD(+) and EVD(-). This means we have a binary classification problem. The problem is that with our initial data set we have 417 EVD(-) patients and only 157 EVD(+) ones. So both classes are not evenly represented. The algorithm could very well just always return EVD(-) and we would get a 72% correct prediction (417/575). So to counter this effect we can use the class weight hyper-parameter and so the algorithm will increase the importance of the EVD(+)  class.

3. Choosing the correct values for the hyper-parameters is not easy and needs to be tested. A technique called grid search can optimize that. We will create several possible hyper-parameter values and grid search will test them all and return the best combination. 

4. Training and testing a prediction on the same data would be a big mistake. You could perfectly predict the data since you trained your model with it. It would give you the impression of a very good prediction, which could not be the case. That is why we randomly split our data in a training and testing set. We use 70% of the data to train the model and 30% to test it. Since we have few data we want to repeat this training/testing to obtain a mean test result. This is called cross validation. 

5. Once the optimal parameters were found we will this time use the whole all patients to train our final model with the optimal hyper-parameters. This is the model we will compare to the current prediction used in the app and see if we improved our prediction score.

6. The two models are compared by calculating the Area Under the Receiver Operating Characteristics (ROC) Curve (AUC). 

7. If we obtained a better score we will then export the model (basically giving weights to each symptom) and make it available for the users.
 
#References

1.	Lado M, Walker NF, Baker P, Haroon S, Brown CS, Youkee D, et al. Clinical features of patients isolated for suspected Ebola virus disease at Connaught Hospital, Freetown, Sierra Leone: a retrospective cohort study. The Lancet Infectious diseases. 2015 Sep;15(9):1024-33. PubMed PMID: 26213248.

2. 	Hartley M-A, Young A, Tran A, Okoni-Williams H-H, Suma M, Mancuso B, et al. Predicting Ebola Infection: A Malaria-Sensitive Triage Score for Ebola Virus Disease. (Accepted) PLoS Neglected Tropical Disease. 2017.

3.	Hartley M-A, Young A, Tran A-M, Okoni-Williams HH, Suma M, Mancuso B, et al. (2017) Predicting Ebola Severity: A Malaria-Sensitive Triage Score for Ebola Virus Disease. PLoS Negl Trop Dis (Accepted)








