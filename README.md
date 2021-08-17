# Inwentaryzacja

[EN]
This project is created for PFHBiPM - https://pfhb.pl/
It's a inventory system to keep in order items. It's connected with FireBase-FireStore NoSQL Online database.
It uses the camera to scan barcodes. After succesful scan, program gets data from Firestore and if there are data then u can fill quantity label and send raport. If there are no data available then u have to put name, quantity and minimal amount and then u can send raport. 
If after sending raport, quntity will be lower than minimum then it automatic generates an email. 
You can add printer and set dependence items. It will be attached to the email generated.
There is too a logging system and reports system.

Thanks for patience!


[PL]
Projekt stworzony dla PFHBiPM - https://pfhb.pl/
Jest to system inwentaryzacji, stworzony z myślą o regulowaniu ilości sztuk różnych materiałów eksploatacyjnych. 
Aplikacja łączy się z FireBase-Firestore NoSQL Online bazą danych.
W projekcie jest używana kamera w celu skanowania kodów kreskowych. Po zeskanowaniu kodu kreskowego, pojawi się on w odpowiednim miejscu.
Teraz wystarczy wpisać ilość sztuk danego przedmiotu, wybrać jego typ i wysłać do bazy danych.
W przypadku gdy dany przedmiot nie posiada przypisanego typu, wystarczy wpisać typ, ilość i wartość minimalną. 
Po wysłaniu raportu, program sprawdza czy ilość przedmiotu nie jest mniejsza od wartości minimalnej, jeśli tak to utworzy on automatycznie
email w celu domówienia brakującego materiału.
Za pomocą odpowiedniej zakładki można przejść do sekcji drukarek. Przy dodawaniu drukarki można podać jej kompatybilne materiały.
Będzie to miało wpływ na generowane email-e. 
W aplikacji jest zaimplementowany system logujący akcje na serwerze bazodanowym. 
Jest również system raportów, aby śledzić jak zmienia się stan magazynowy przedmiotu przy pomocy wykresu.

Dziękuję za uwagę!
