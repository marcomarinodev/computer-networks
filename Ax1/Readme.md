## Text
Scrivere un programma che attiva un thread T che effettua il calcolo
approssimato di PI. Il programma principale riceve in input da linea di comando
un parametro che indica il grado di accuratezza (accuracy) per il calcolo di PI
ed il tempo massimo di attesa dopo cui il programma principale interomp
thread T.
Il thread T effettua un ciclo infinito per il calcolo di PI usando la serie di
Gregory-Leibniz ( PI = 4/1 – 4/3 + 4/5 - 4/7 + 4/9 - 4/11 ...).
Il thread esce dal ciclo quando una delle due condizioni seguenti risulta
verificata:
+ Il thread è stato interrotto
+ La differenza tra il valore stimato di PI ed il valore Math.PI (della libreria JAVA) è minore di accuracy

### Solutions

Mandare interruzione dal main thread a quello che approssima il PI. Il thread approssimatore si interrompe anche
quando raggiungere una tolerance pari all'accuratezza passata attraverso il costruttore del runnable.

### Test Cases
### Test 1
+ Timeout: 200ms
+ Accuracy: 0.00000000000000000000000000000000000000000000000001
### Test 2
+ Timeout: 2000ms
+ Accuracy: 0.01
