
# Technische Doku

Unser Applikation ist in Java geschrieben. Als Buildsystem verweden wir Gradle damit wir die Java Applikation erstellen können.

Gielderung der Applikation:
    TODO BILD HINZUFüGEN SYSTEMüBERSICHT
    TODO BILD HINZUFüGEN SCHEMA
    TODO BILD HINZUFüGEN KLASSEN DIAGRAM

Systemübersicht:
- [Discord API](#discord-API)
- [Raspberry PI / Server](#raspberry-pi-server)
- [Docker](#docker)
- [Applikation, Dependencies](#applikation)
- [Reflektion](#reflektion)


## Discord API
Wir verwenden die Discord API via der Java Biliothek discord4j.

### Risiken:

Sollte die Discord API down sein geht unser Bot natürlich nicht somit ist es ein single point of failure.

## Raspberry PI Server
Als Server verwenden wir einen Raspberry PI 4B 8 GB RAM.
### Vorteile:
Dieser hat den Vorteil wenig Ressourcen zu verbrauchen (Strom, Platz). Ideal Für unseren Use Case.
### Nachteile 
Ein Nachteil kann sein, dass bestimmte Software nicht läuft, da es eine andere CPU Architecture hat. 
Beim Raspberry PI ist es ein arm64v7. 

Uns betrifft das nicht in diesem Fall, da unsere Java Applikation in einem Docker Container gehostet wird.

### Risiken:
1. Stromausfall: Sollte es einen Stromausfall geben, wird der Discord Bot ,abstürtzen da wir ihn nicht redudant hosten.
2. Internetausfall: Sollte <TODO MARIO ISP> ausfallen ist der Discord Bot auch nicht erreichbar.


## Docker
Wir verwenden docker als Container Technologie. 
Ermöglicht uns einfaches Deployment von der Applikation. Wenn Die Applikation auf dem Container Läuft, kann der Discord Bot auf allen Docker lauffähigen PCs gehostet werden. 
Zudem verwenden wir noch Docker Compose. Dieser Managed den Docker Container. Sollte dieser abstürtzen wird er neu gestartet. Dies Ermöglicht es uns eine hohe Uptime zu gewäreleisten.

### Vorteile:
Abgestürzte Container werden neu gestartet.
### Nachteile / Risiken:
Wir haben eine zusätzliche Abhänigkeit welche wir managen müssen.


## Applikation
Unser Discord Bot ist in Java geschrieben. Wir verwenden als biliothek discord4j, welche uns eine einfache integration mit Discord ermöglicht.
### Vorteile:
Java ist eine einfach zu lernende Sprache. Weiter gibt es schon viele Biliotheken, welche wir einbinden können.
### Nachteile / Risiken:
Wir haben eine direkte Abhängigkeit zu discord4j. Sollte die Biliothek nicht mehr gewartet werden, müssten wir Anspassungen selbst implentieren.

## VCS
Als VCS haben wir die Vorgabe Git

### Vorgehen beim Programmieren

Wir haben uns dazu entschieden die Applikation in Pair Programming zu erstellen. Somit haben wir einen direkten Austausch von Ideen, wie wir unser gewünschtes Feature umsetzen können.
Der Vorteil dabei ist, dass wir beide den Code kennen und diesen einfach warten können.


```Filesystem
main/
└── Java/
      └── ch.wema/
            ├── commands/
            │        └── PingCommand
            ├── core/
            │    ├── command/
            │           └── Command
            │    └──reaction/
            │           └── Reaction
            ├── event.listeners/
            │           ├── ChatInpitInteractionEventListener
            │           ├── MessageCreateEventListener
            │           └── VoiceStateUpdateEventListener
            └──  reactions/
                  │     ├── AddEveryoneReaction
                  │     ├── SimpReaction
                  │     └── YepReaction
                  ├── GlobalCommandRegistrar
                  └── Sugu
 
```


Folgende 4 Teile sind die wichtigsten:

1. Commands
2. Reactions
3. Listeners
4. GlobalCommandRegistrar


#### Commands:

Commands oder auch Befehle sind alle Befehle welcher der Benutzer kontrolliert absetzen kann.<br />
Diese unterleidung dient zur seperierung von Klassen und dient der einfachen Übersicht.

#### Reactions:

Eine Reaction ist ein Event, auf welches man Reagieren möchte bsp. Statusänderung von Usern.

#### Listeners:

Listeners handeln alle Events von Reactions und Commands.

#### GlobalCommandRegistrar:
Dient Regestierung von Events auf der Server Seite.


#### Gliederungsvorteile

Anpassungen sind einfach zu erstellen, da man diese als Command oder als Reaction einfach hinzufügen kann und keinen bestehenden Code anpassen muss.
Der der Core Ordner dient als Abstraction damit alle Reactions/Commands einen gleichen nenner haben ausserhalb des Objektes.

## Reflektion

#### Was ist gut geganen: TODO

#### Wo haben wir noch verbessung potenzial: TODO

#### Offne Punkte: TODO

#### Aussicht: TODO