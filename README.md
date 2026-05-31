# 🗺️ MapTracker — Lab 12 : GPS et Google Maps

## Objectif

Construire une application Android intégrant **Google Maps** pour afficher la position GPS en temps réel. L'application écoute les changements de localisation, place un marqueur sur la carte et propose d'activer le GPS s'il est désactivé.

---

## Concepts Abordés

- Intégration de **Google Maps SDK for Android**
- `SupportMapFragment` et callback `OnMapReadyCallback`
- `LocationManager` et `LocationListener`
- Permissions runtime (localisation)
- `CameraUpdateFactory` pour zoomer et animer la caméra
- `AlertDialog` pour proposer l'activation du GPS
- Marqueur unique qui se déplace avec la position

---

## Aperçu de l'Application

**MapTracker** est une application de cartographie en temps réel :

| Élément              | Description                                         |
|---------------------|-----------------------------------------------------|
| Titre               | "MapTracker" affiché en haut en bleu foncé          |
| Texte de position   | Affiche latitude et longitude en temps réel         |
| Carte Google Maps   | Occupe tout l'écran, centrée sur Marrakech par défaut |
| Marqueur            | Se déplace automatiquement à chaque nouvelle position |
| Dialog GPS          | S'affiche si le provider est désactivé              |

---

## DEMO 


https://github.com/user-attachments/assets/d9f84017-b6f7-4e01-9a6e-fd207463def4



## Structure du Projet

```
MapTracker/
├── java/com/example/maptracker/
│   └── MainActivity.java
├── res/
│   ├── layout/
│   │   └── activity_main.xml
│   └── values/
│       ├── colors.xml
│       ├── strings.xml
│       └── themes.xml
└── AndroidManifest.xml
```

---

## Permissions Requises

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Configuration de la Clé API

La clé Google Maps API est déclarée dans `AndroidManifest.xml` à l'intérieur de la balise `<application>` :

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="VOTRE_CLE_API" />
```

> ⚠️ La clé doit obligatoirement être à l'intérieur de `<application>`, pas à l'extérieur.

---

## Détails Clés de l'Implémentation

### Initialisation de la carte
```java
SupportMapFragment mapFragment = (SupportMapFragment)
        getSupportFragmentManager().findFragmentById(R.id.map_fragment);
mapFragment.getMapAsync(this);
```

### Marqueur unique qui se déplace
```java
if (currentMarker == null) {
    currentMarker = mMap.addMarker(new MarkerOptions()
            .position(updatedPosition)
            .title(getString(R.string.marker_title)));
} else {
    currentMarker.setPosition(updatedPosition);
}
```

### Animation de la caméra
```java
mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updatedPosition, 15f));
```

### Dialog GPS désactivé
```java
private void buildAlertMessageNoGps() {
    new AlertDialog.Builder(this)
        .setMessage(getString(R.string.gps_disabled))
        .setPositiveButton(getString(R.string.gps_yes), (d, i) ->
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
        .setNegativeButton(getString(R.string.gps_no), (d, i) -> d.cancel())
        .show();
}
```

---

## Dépendances

```gradle
implementation 'com.google.android.gms:play-services-maps:18.2.0'
implementation 'com.google.android.gms:play-services-location:21.2.0'
```

---

## Choix de Design

- **Thème :** Clair / Bleu Marine Professionnel
- **Palette de couleurs :** Bleu marine (`#1A237E`), Ambre (`#FF6F00`), Fond (`#E8EAF6`)
- **Position par défaut :** Marrakech, Maroc (`31.6295, -7.9811`)
- **Niveau de zoom :** 15 (niveau rue/quartier)

---

## Comment Exécuter

1. Cloner ou ouvrir le projet dans **Android Studio**
2. Ajouter votre clé API Google Maps dans `AndroidManifest.xml`
3. Vérifier que le Min SDK est défini à **24**
4. Lancer sur un émulateur ou un appareil physique (Android 7.0+)
5. Accepter les permissions de localisation
6. Pour simuler une position sur l'émulateur : **Extended Controls → Location → Set Location**

---

## Référence du Lab

- **Numéro du lab :** 12
- **Titre :** GPS et Map (Google Maps Activity)
- **Langage :** Java
- **Min SDK :** 24 (Android 7.0 Nougat)
- **API :** Google Maps SDK for Android
