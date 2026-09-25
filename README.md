# Bibliotekshanteraren

En konsolapplikation (CLI) i Java för att administrera ett litet bibliotek: böcker, medlemmar
och utlåning. Programmet körs i en menyloop tills användaren väljer att avsluta. Det är byggt
med vanliga arrayer, utan Collections Framework, som en övning i Javas grunder.

## Funktioner

- **Lägg till bok** – ISBN tilldelas automatiskt (börjar på 101)
- **Registrera medlem** – medlems-id tilldelas automatiskt (börjar på 1)
- **Låna bok** – kontrollerar att boken och medlemmen finns, att boken inte redan är utlånad och att medlemmen inte har nått lånegränsen (max 2 aktiva lån)
- **Lämna tillbaka bok**
- **Sök bok** – på del av titel eller författare, skiftlägesokänsligt
- **Visa alla böcker** – med status (tillgänglig, eller utlånad och till vem)
- Exempeldata (6 böcker och 3 medlemmar) laddas vid start

## Projektstruktur

| Klass | Ansvar |
|-------|--------|
| `Book` | Record som håller en boks ISBN, titel och författare |
| `Member` | Klass som håller en medlems id, namn och antal aktiva lån |
| `Library` | All logik: lagrar böcker och medlemmar i arrayer och hanterar lån och sökning |
| `Main` | Menyloopen och all inläsning via `Scanner` |

## Designval och reflektion

### Book som record
En bok ändras aldrig efter att den har skapats, den är bara data. Därför passar en record bra:
den är oföränderlig, och Java genererar automatiskt konstruktor, accessor-metoder, `equals()`
och `toString()`, så klassen behöver nästan ingen kod. Om en bok är utlånad lagras inte i
boken själv utan i `Library`, och det är just det som gör att `Book` kan förbli oföränderlig.

### Member som klass
En medlems antal aktiva lån ändras över tid, så en record fungerar inte här. `Member` är en
vanlig klass med privata fält och get-metoder. Istället för en vanlig setter för antalet lån
använder jag `increaseLoans()` och `decreaseLoans()`, så värdet bara kan ändras ett steg i
taget och aldrig sättas till något ogiltigt. Metoden `canBorrow()` håller regeln för
lånegränsen i den klass som äger datan. Det är inkapsling.

### Lagring av böcker, medlemmar och lån
Böcker och medlemmar lagras i arrayer med fast storlek (30 böcker, 10 medlemmar), med en
räknare som håller reda på hur många platser som används. Om en array är full får användaren
ett meddelande istället för att programmet kraschar.

Lånen lagras i en parallell array, `borrowedBy`, där `borrowedBy[i]` håller medlems-id för
den som har lånat `books[i]`, eller `-1` om boken är tillgänglig. Lösningen är enkel och
snabb, men nackdelen är att de två arrayerna alltid måste hållas i synk.

### Ansvarsfördelning
`Main` sköter bara menyn och inläsningen, medan `Library` innehåller logiken. Upprepad kod i
`Library` har brutits ut till privata hjälpmetoder (`findBookIndex`, `findMember`,
`printBookWithStatus`), så varje sökloop bara finns på ett ställe.

### Hantering av inmatning
All inmatning läses med `nextLine()`. Menyvalet läses som en `String`, så bokstäver kan aldrig
orsaka en krasch; ett ogiltigt val hamnar helt enkelt i `default`. Tal omvandlas med
`Integer.parseInt()` inuti `try/catch`, och användaren får försöka igen om inmatningen inte är
ett giltigt tal. Det undviker också det vanliga problemet där `nextInt()` lämnar kvar en
radbrytning i inmatningen.

### Automatiskt ISBN och medlems-id
Programmet tilldelar ISBN och medlems-id själv istället för att låta användaren skriva in dem.
Det förhindrar dubbletter och ogiltiga värden, som annars till exempel skulle kunna göra att
en sökning på ISBN hittar fel bok.