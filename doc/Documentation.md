<p align="center">
  <a href="https://example.com/">
    <img src="https://via.placeholder.com/72" alt="Logo" width=72 height=72>
  </a>

  <h3 align="center">Logo</h3>

  <p align="center">
    Short description
    <br>
    <a href="https://reponame/issues/new?template=bug.md">Report bug</a>
    ·
    <a href="https://reponame/issues/new?template=feature.md&labels=feature">Request feature</a>
  </p>
</p>


## Table of contents

- [Quick start](#quick-start)
- [Status](#status)
- [What's included](#whats-included)
- [Bugs and feature requests](#bugs-and-feature-requests)
- [Contributing](#contributing)
- [Creators](#creators)
- [Thanks](#thanks)
- [Copyright and license](#copyright-and-license)


## Einleitung

Diese Dokument dient als Projektdefinition für unsere Projekt Arbeit in Modul Projektarbeit. In diesem Dokument wir die Aufgabe erläutert, unsere Idee und die zu verwendeten Techologien erklärt. Ausserdem werden die Rahmenbedingungen erkläutert.

## Aufgabenstellung

Ziel ist es in eine Applikation in JAVA zur erstellen welches das Administeren von Discord Server vereinfacht.

## IDEE






## Projektziele

Durch Automatisation und Implementation von Tasks und Features (Rollenverteilung, Channelmanagement) soll der Verwaltungsaufwand verringert werden.

Mit diesem Projekt sollen folgende Ziele erreicht werden:
- 1.	Automatisation von Rollenverteilung
- 2.	Individuelle Befehle (Ping - Pong)
- 3.	Aufbau der Infrastruktur für eine erweiterbare Lernumgebung

# Risikoanalyse

Schadensausmass
| | | | |
|:---|:---|---:|---:|
Hoch|-Discord stellt keine Dienste <br />mehr bereit <br />-Java wird nicht weiter<br /> unterstützt| |
Mittel| | |
Niedrig| |   |
| |Niedrig |Mittel |Hoch| 
||Eintrittswarscheinlichkeit| |



# Meilenstein Chart
| Meilenstein | Festgelegter Termin |Fertigstellung |
|-------------|----------------------|-------------------|
|Projektbeginn|29.04.2022|                   |
|Projektantrag Abgabe|06.05.2022|                   |
|Infrastruktur bereitgestellt|27.05.2022|30.07.2022                   |
|Projektabschluss|02.09.2022|                   |

# Termine

| Sitzungen |Termine | 
|----------------|----------------------|
|Projektsitzungen|03.06.2022, 29.08.2022|                   
|Präsentation|06.05.2022|                   



https://www.tomshardware.com/reviews/raspberry-pi-set-up-how-to,6029.html


Here goes all the budgets
# Raspberry PI Einrichtung

Für den Discord Bot benötigen wir einen Host. Wir haben uns hier für ein Raspberry PI entschieden, da dieses einfach zu warten ist und wenig Platz benötigt.
Mit Hilfe von Raspberry PI Imager kann das gewünschte Betriebssystem installiert werden. Wir verwenden das PI OS 64-bit und schreiben dieses auf die Micro SD Karte.
Bei erfolgreichem schreiben des Betriebssystems können wir die SD Karte einsetzen und mit einem USB-C den Raspberry Pi Computer mit Strom versorgen.

# Raspberry Konfiguration

Mittels HDMI können wir den Desktop des Betriebssystem displayen und die ersten Konfigurationen vornehmen. Mittels Setupmanager wählen wir als erstes die korrekte Region, Zeitzone und Sprache. Danach setzen wir ein Passwort sowie den Namen für den User. Wir benennen ihn nach unserem Projekt, also sugu. Wir benötigen den Namen und Passwort später, wenn wir via SSH darauf zugreifen wollen. Wir setzen den Haken für das Screen Setup um optimierte Auflösungen zu erhalten für den Bildschirm. Da wir Ethernet verwenden, müssen wir uns nicht mit einem W-LAN verbinden. <br />
Sobald wir mit einem Netzwerk verbunden sind, können wir im Setupmanager ein Update der Software durchführen.

# SSH einschalten
Um auf unseren User zugreifen zu können, müssen wir SSH aktivieren. Dafür clicken wir auf dem Desktop auf das Raspberry logo, wählen **Preferences** und dann **Raspberry PI Configuration**. Unter dem **Interfaces** Tab können wir SSH anwählen. 

# Statische IP vergeben
Um mit keinen Komplikationen konfrontiert zu werden, vergeben wir dem Raspberry PI eine Statische IP Addresse. Dafür öffnen wir das Terminal auf dem Desktop. Mittels ``ifconfig`` können wir die momentan zugeweiste IP des netzwerkes ansehen. In unserem Fall ist dies **192.168.1.17**.<br />Mittels ``sudo nano /etc/dhcpcd.conf`` können wir direkt in die config file unsere gewünschte IP Addresse schreiben. Dies machen wir wiefolgt: 
```
interface eth0
static ip_address=192.168.1.17
static routers=192.168.1.1
static domain_name_servers=8.8.8.8 8.8.4.4

```
Einstellungen mit ``Ctrl + o`` schreiben und den Editor mit ``Ctrl + x`` verlassen. Danach den Raspi neustarten:

``sudo reboot``

# Docker Installation
Da nun eine Statische IP gesetzt wurde, kann via SSH verbunden werden. Mittels dem Programm Putty auf 192.168.1.17 verbinden, Username und Passwort eingeben. <br />
Danach führen wir standart package updates aus, um auf dem neusten stand zu sein.

```
sudo apt update

sudo apt upgrade
```

Mit dem curl statement starten wir ein Shell Script, um Docker zu installieren: 

```
curl -sSL https://get.docker.com | sh
```
Mit ```docker``` können wir überprüfen, ob docker korrekt installiert wurde. 
Als nächstes müssen wir den Benutzer ```sugu``` in die dockergruppe hinzufügen:
```
sudo usermod -aG docker sugu
```
# Portainer Installation
Um die Verwaltung der Container zu vereinfachen mit einem web GIU installieren wir Portainer:


```
sudo docker pull portainer/portainer-ce:linux-arm

sudo docker run -d -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer-ce:linux-arm
```
Danach ist die Installation abgeschlossen. Nun können wir mit einem Browser auf das webinterface zugreifen: ``http://sugu.local:9000/``


## What's included

Some text

```text
folder1/
└── folder2/
    ├── folder3/
    │   ├── file1
    │   └── file2
    └── folder4/
        ├── file3
        └── file4
```

## Bugs and feature requests

Have a bug or a feature request? Please first read the [issue guidelines](https://reponame/blob/master/CONTRIBUTING.md) and search for existing and closed issues. If your problem or idea is not addressed yet, [please open a new issue](https://reponame/issues/new).

## Contributing

Please read through our [contributing guidelines](https://reponame/blob/master/CONTRIBUTING.md). Included are directions for opening issues, coding standards, and notes on development.

Moreover, all HTML and CSS should conform to the [Code Guide](https://github.com/mdo/code-guide), maintained by [Main author](https://github.com/usernamemainauthor).

Editor preferences are available in the [editor config](https://reponame/blob/master/.editorconfig) for easy use in common text editors. Read more and download plugins at <https://editorconfig.org/>.

## Creators

**Creator 1**

- <https://github.com/usernamecreator1>

## Thanks

Some Text

## Copyright and license

Code and documentation copyright 2011-2018 the authors. Code released under the [MIT License](https://reponame/blob/master/LICENSE).

Enjoy :metal: