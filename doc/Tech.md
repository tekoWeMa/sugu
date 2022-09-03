Technischer teil aufbau von der Applikation des
Unser Applikation ist in Java geschrieben als Buildsystem verweden wir Gradle damit wir die Java Applikation erstellen können.
Wieso haben wir uns für Java entschieden dies war eine vorraussetztung. 

Gielderung der Applikation:
    TODO BILD HINZUFUEGEN SYSTEMUEBERSICHT
    TODO BILD HINZUFUEGEN SCHEMA
    TODO BILD HINZUFUEGEN KLASSEN DIAGRAM

Systemuebersicht:
    Wie man sieht gibt es vier Kompenten
    Discord API
    Raspberry PI / Server
    Docker
    Applikation in JAVA / Dependencies

Discord API:
    Wir verwenden die Discord API via der Java biliotek discord4j.
    Risiken:
        Sollte die Discord API down sein geht unser Bot natuerlich nicht somit ist es ein single point of failure.

Raspberry PI / Server:
    Als Server verwenden wir einen Raspberry PI 4B 8 GB RAM. Dieser hat denn vorteil wenig resoursen zu verbrauchen (Strom).
    Was aber ein nachteil sein kann das die meisten Software nicht laufen da es einen andre CPU Architecture ist es ist ein arm64v7. Aber uns betrieft das nicht da wir unser Software in einen Docker Container hosten und in diesen Container lauft die Java applikation.
    Risiken:
        Stromausfall: Sollte es ein Strom ausfall geben wird der Discord Bot abstuertzen da wir ihn nicht redudant hosten.
        Internetausfall: Sollte <TODO MARIO ISP> ausfallen ist der Discord Bot auch nicht erreichbar.
    Vorteile:
        Sehr Strom sparend somit Idieal fuer unseren Use Case.

Docker:
    Wir verwenden docker als Container Technokie. Ermoeglicht uns einfaches Deployment von der Applikation sowie wenn es auf einen PC lauft es auf jeden PC welcher docker lauffaehig ist. Zudem verwenden wir noch Docker Compose dieser mangemnt denn Docker Container sollte dieser abstuertzen wird neu gestarten diese Ermoeglicht es uns einen hohe Uptime zu gewaereleisten.
    Nachteile / Risiken:
        Wir haben einen zusaetzliche abhaenigkeit welche wir mangement muessen.
    Vorteile:
        Container welche abstuertzen werden Automatisch neu gestarten.

Applikation:
    Unser Discord Bot ist in Java geschrieben. Wir verwenden als biliotek discord4j welche uns eine einfache integration mit Discord ermoeglicht.
    Nachteile / Risiken:
        Wir haben eine direkte abhaenig zu discord4j. Sollte die biliotek nicht mehr gewartet werden muessten wir die anspassungen selbst implentieren.
    Vorteile:
        Java ist eine einfach sprache zulernen und es gibt schon viele biliotek welche wir einbinden koennen.

Also VCS haben wir die Vorgabe Git

Wie gehen wir vor mit der Programmierung:
    Wir haben uns dazu entschieden die Applikation in Pair Programming zu erstellen. Somit haben wir einen direkten austausch von Ideen wie wir unser gewuenschten feature umsetzen koennen.
    Vorteil ist es das wir beide somit automatisch denn Code kennen und beide diesen einfach warten koennen.

ToDo Projekt struture / File System darstellen.
Wie mann Sieht gibt es 4 wichtigeteile

1. Commands
2. Reactions
3. Listeners
4. GlobalCommandRegistrar

Commands:
    Was genau ist einen Command. Commands oder auch Befehle sind alle Befehle welcher der Benutzer kontrolliert absetzen kann.
    Diese unterleidung dient zur seperierung von Klassen und dient der einfachen Übersicht.
Reactions:
    Eine Reaction ist ein Event auf welchen mann Reagieren möchte bsp. Status änderung von Usern.
Listeners:
    Diese handlen alle fuer jedes Event alle Reactions und Commands
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