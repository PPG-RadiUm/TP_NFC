# TP NFC

## Introduction
### Qu’est ce que le NFC ?
Le NFC, ou Near Fields Communication (Communication en champ rapproché) est une technologie de communication sans fil à courte portée et à haute fréquence, permettant l'échange d'informations entre des périphériques jusqu'à une distance d'environ 10 cm.
C’est une forme de RFID (Radio Frequency IDentification), cependant la distance d’échange de données est bien moins importantes que la plupart des autres dispositifs RFID, qui est de 2m à 10m généralement.


On distingue trois types de fonctionnement des équipements NFC (Ou étiquettes/tags) : 
- Le mode émulation de carte
- Le mode lecteur
- Le mode Peer-to-Peer
En mode émulation de carte, le device NFC est dit “passif” : il se comporte comme une carte à puce sans contact et est donc cible des autres devices NFC.
En mode lecteur, le device NFC émet un champ magnétique pour initier la communication avec un tag NFC et lui envoie une commande. C’est un fonctionnement de lecteur de carte et le device est dit “actif” car il envoie des informations.
En mode Peer-to-Peer les deux devices NFC prennent tours à tours le rôle “émulateur de carte” et “lecteur”, ils communiquent entre eux et s’échange des informations. C’est l’utilisation que nous pouvons avoir en échangeant des coordonnées GPS par Google Maps par exemple, en collant dos à dos les smartphones.


### Mais à quoi ça sert concrètement ?
Le NFC permet d’échanger des données entre deux appareils équipés.
Les utilisations les plus répandues sont :
Paiement : Le paiement sans contact des cartes bancaires ou étudiantes (IZLI et l’ancienne également)
Passe/identification : Ouvrir des portes, ...
Échange de données : Échanger des contacts deux smartphones dos à dos
…


Sur un appareil android il existe des applications dédiés pour lire/écrire sur un tag NFC, définir des actions à la suite de la lecture d’un tag NFC, …
Sur des applications comme Google Maps, nous pouvons échanger les coordonnées marqués d’un device à un autre.

## Objectifs du TP
L’objectif de ce TP est de se familiariser avec l’utilisation du NFC sur Android.
Nous verrons comment activer le NFC (si l’appareil le permet) et comment recevoir des intents au contact d’un tag NFC (nous utiliserons la carte étudiants ;) ).

## Déroulement du TP

### Initialisation du projet
On va tout d’abord créer un projet sous Android Studio en sélectionnant l’API 10 minimum visant Android 2.3.3 puis en choisissant “Empty Activity”.
Commençons par paramétrer notre application pour manipuler le NFC.
Dans *AndroidManifest.xml*, ajoutez ces lignes :

```xml
<uses-permission android:name="android.permission.NFC"/>
<uses-feature android:name="android.hardware.nfc"
   android:required="false"/>
```

Ces lignes permettent de demander l’utilisation du NFC s’il est disponible sur l’appareil et interdisent l’affichage dans le playstore de notre application aux devices n’étant pas équipé du NFC.

### Vérification de la compatibilité et de l’activation du NFC


Direction *MainActivity.java*. Le but va être maintenant de vérifier si l’appareil possède le NFC et s’il le possède, savoir s’il est activé. Pour cela nous allons copier ces lignes dans le *onCreate* :

```java
nfcAdapter = NfcAdapter.getDefaultAdapter(this);
textView = (TextView) findViewById(R.id.textView);
```

```java
// Vérifie si l'appareil a le NFC activé et s'il est activé
if(nfcAdapter == null){
   Toast.makeText(this, "NFC n'est pas disponible :'(", Toast.LENGTH_LONG).show();
}else if(nfcAdapter.isEnabled()){
   Toast.makeText(this, "NFC activé", Toast.LENGTH_LONG).show();
}else{
   Toast.makeText(this, "NFC non activé", Toast.LENGTH_LONG).show();
}
```

Sur le code ci-dessus, on vérifie dans le premier if si notre *nfcAdapter* est égal à null, auquel cas celà voudrait dire que notre appareil ne prend pas en charge NFC.
Ensuite le else if est pour le cas ou le *nfcAdapter* est différent de null et est activé. Enfin le else pour le cas ou le NFC n’est pas activé.


Rien ne vous échappe … Effectivement, nous n’avons pas instancié notre variable *nfcAdapter*. Nous plaçons donc la ligne ci-dessous au dessus de la méthode onCreate, toujours dans notre *MainActivity.java*. Nous parlerons par la suite de la variable *textView*.

```java
NfcAdapter nfcAdapter;
TextView textView;
```

### Réception d’un intent NFC


A partir d’ici, vous pouvez d’ores et déjà tester l’application pour savoir si votre appareil est compatible NFC. Maintenant que notre objet NFC est initialisé et que nous sommes au courant de la compatibilité et de la mise en marche de notre NFC, nous pouvons maintenant être signalé lors d’un contact avec un tag NFC.


Pour cela nous allons écrire trois méthodes :
- onResume
- onPause
- onNewIntent


Copiez-collez ce code correspondant à la méthode onResume (toujours dans *MainActivity.java*) :

```java
@Override
protected void onResume() {
   Intent intent = new Intent(this, MainActivity.class);
   intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

   PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
   IntentFilter[] intentFilter = new IntentFilter[]{};

   if(nfcAdapter != null) {
       nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
   }

   super.onResume();
}
```


Dans le code ci-dessus nous créons l’intent et ensuite nous vérifions que le NFC est disponible sur l’appareil pour utiliser la méthode *enableForegroundDispatch* pour activer la réceptions de tags NFC pour l’activity en question.


Ensuite copiez ce code à la suite du précédent :

```java
@Override
protected void onPause() {
   if(nfcAdapter != null) {
       nfcAdapter.disableForegroundDispatch(this);
   }

   super.onPause();
}
```

Ce code contenant la méthode contraire à *enableForegroundDispatch* désactive la reception de tag NFC pour cette activity.


Enfin, la méthode gérant l’action à la suite de la réception d’un intent d’un tag NFC :

```java
@Override
protected void onNewIntent(Intent intent) {
   // Permet de notifier la reconnaissance d'un TAG NFC
   Toast.makeText(this, "NFC intent reçu!", Toast.LENGTH_LONG).show();

textView = (TextView) findViewById(R.id.textView);
textView.setText(textView.getText() + "\n" + formatTimeToFrance());

   super.onNewIntent(intent);
}
```

Dans ce code, nous envoyons tout simplement un toast à l’activity, et nous affichons dans le textView (le fameux textView du début) l’heure à laquelle nous avons reçu l’intent grâce à une fonction que nous allons implémenté juste après notre textView. 


Nous allons donc rajouter notre textView. Direction *activity_main.xml* :

```xml
<TextView
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:layout_alignParentTop="true"
   android:layout_alignParentLeft="true"
   android:layout_alignParentStart="true"
   android:id="@+id/textView"
   android:layout_alignParentRight="true"
   android:layout_alignParentEnd="true"
   android:layout_alignParentBottom="true"
   android:text="Intent reçus: " />
```

Il nous faut donc maintenant la magnifique fonction *formatTimeToFrance* :
(Il existe des méthodes plus “sympa” pour formater une date mais elle nécessitent des API plus élevés. Pour l’usage du TP, cette fonction fera l’affaire :) ).

```java
// Cocorico
protected String formatTimeToFrance() {
   long millis = System.currentTimeMillis();
   return String.format("%02d:%02d:%02d", (TimeUnit.MILLISECONDS.toHours(millis) % 24) + 1,
           TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
           TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
}
```


Vous pouvez tester à nouveau l’application (si vous disposez du NFC c’est plus intéressant) et passer votre carte étudiant (ou carte bancaire, passeport; …) derrière votre appareil Android. Vous constaterez un joli toast qui spécifie que votre appareil reconnaît la carte comme un tag NFC et vous avez un historique des intents.

## Fin du TP
Merci de votre attention, j'espère que ce TP vous aura plu et que vous aurez appris des choses.
Pour conclure je dirais que le NFC n’est pas encore très répandu et connu des utilisateurs, même ceux qui disposent du NFC mais les possibilités sont nombreuses. 
Cette technologie peut faciliter le transfert de petites informations et donne accès à de nouveaux moyens de partage rapides.
Pour plus de renseignements je vous invite à consulter la doc Android Developpers sur le sujet du NFC : https://developer.android.com/guide/topics/connectivity/nfc/nfc.html
Voici la liste des téléphones compatibles NFC : http://www.nfcworld.com/nfc-phones-list/
