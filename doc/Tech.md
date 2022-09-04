Technischer Teil aufbau von der Applikation

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


## Discord API
Wir verwenden die Discord API via der Java biliotek discord4j.

Risiken:
Sollte die Discord API down sein geht unser Bot natürlich nicht somit ist es ein single point of failure.

## Raspberry PI Server
Als Server verwenden wir einen Raspberry PI 4B 8 GB RAM. Dieser hat denn vorteil wenig resoursen zu verbrauchen (Strom).
Was aber ein nachteil sein kann das die meisten Software nicht laufen da es einen andre CPU Architecture ist es ist ein arm64v7. Aber uns betrieft das nicht da wir unser Software in einen Docker Container hosten und in diesen Container lauft die Java applikation.
Risiken:
Stromausfall: Sollte es ein Strom ausfall geben wird der Discord Bot abstürtzen da wir ihn nicht redudant hosten.
Internetausfall: Sollte <TODO MARIO ISP> ausfallen ist der Discord Bot auch nicht erreichbar.
Vorteile:
Sehr Strom sparend somit Idieal für unseren Use Case.

## Docker
Wir verwenden docker als Container Technokie. 
Ermöglicht uns einfaches Deployment von der Applikation sowie wenn es auf einen PC lauft es auf jeden PC welcher docker lauffähig ist. Zudem verwenden wir noch Docker Compose dieser mangemnt denn Docker Container sollte dieser abstürtzen wird neu gestarten diese Ermöglicht es uns einen hohe Uptime zu gewäreleisten.

Nachteile / Risiken:
Wir haben einen zusätzliche abhänigkeit welche wir managen müssen.
Vorteile:
Container welche abstürtzen werden Automatisch neu gestarten.

## Applikation
Unser Discord Bot ist in Java geschrieben. Wir verwenden als biliotek discord4j welche uns eine einfache integration mit Discord ermöglicht.
    Nachteile / Risiken:
        Wir haben eine direkte abhänig zu discord4j. Sollte die biliotek nicht mehr gewartet werden müssten wir die anspassungen selbst implentieren.
    Vorteile:
        Java ist eine einfach sprache zulernen und es gibt schon viele biliotek welche wir einbinden können.

Als VCS haben wir die Vorgabe Git

Wie gehen wir vor mit der Programmierung:
    Wir haben uns dazu entschieden die Applikation in Pair Programming zu erstellen. Somit haben wir einen direkten austausch von Ideen wie wir unser gewünschten feature umsetzen können.
    Vorteil ist es das wir beide somit automatisch denn Code kennen und beide diesen einfach warten können.


```Filesystem
main/
└── Java/
      └── ch.wema/
            ├── commands/
            │        └── PingCommand
            ├── core/
            │    ├── command/
            │           └── command
            │    └──reaction/
            │           └── Reaction
            ├── event.listeners/
            │            ├── ChatInpitInteractionEventListener
            │            ├── MessageCreateEventListener
            │            └── VoiceStateUpdateEventListener
            └──  reactions/
                 │     ├── AddEveryoneReaction
                 │     ├── SimpReaction
                 │     └── YepReaction
                 ├── GlobalCommandRegistrar
                 └── Sugu

```


Wie mann Sieht gibt es 4 wichtigeteile


1. Commands
2. Reactions
3. Listeners
4. GlobalCommandRegistrar

Commands:
    Was genau ist einen Command. 
Commands oder auch Befehle sind alle Befehle welcher der Benutzer kontrolliert absetzen kann.
    Diese unterleidung dient zur seperierung von Klassen und dient der einfachen Übersicht.
Reactions:
    Eine Reaction ist ein Event auf welchen mann Reagieren möchte bsp. Status änderung von Usern.
Listeners:
    Diese handlen alle für jedes Event alle Reactions und Commands
GlobalCommandRegistrar:
    Dient Regestierung von Events auf der Server seite.

Was sind Vorteile von dieser Giederung:
    Anpassungen sind einfach zu erstellen da man dieser als Command oder als Reaction einfach hinzufügen kann und keinen bestehnden code anpassen muss 
    Der der Core Ordner dient als abstraction damit alle Reactions/Commands einen gleichen nenner haben ausser halb von Objekt

Refliktion:
    Was ist gut geganen:
    Wo haben wir noch verbessung potenzial:

Offne Punkte:
Aussicht: