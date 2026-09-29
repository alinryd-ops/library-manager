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
- **Visa alla böcker** – sorterade efter titel, med status (tillgänglig, eller utlånad och till vem)
- **Visa medlem med flest aktiva lån**
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
vanlig klass med privata fält och get-metoder. Fälten `id` och `name` är `final`, eftersom de
aldrig ska ändras efter att medlemmen har skapats. Istället för en vanlig setter för antalet lån
använder jag `increaseLoans()` och `decreaseLoans()`, så värdet bara kan ändras ett steg i
taget och aldrig sättas till något ogiltigt. Metoden `canBorrow()` håller regeln för
lånegränsen i den klass som äger datan. Det är inkapsling.

### Lagring av böcker, medlemmar och lån
Böcker och medlemmar lagras i arrayer som startar med plats för 6 böcker och 3 medlemmar, med en
räknare som håller reda på hur många platser som används. När en array är full skapas en större
array, se avsnittet Dynamisk kapacitet.

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

### Förbättringar under arbetets gång

Koden blev inte som den är nu på första försöket. Här beskriver jag några saker
jag upptäckte under arbetet och hur jag ändrade koden efter det.

I sökmetoden `searchBooks` hade jag själv döpt variabeln för söktexten till `searchWord`. Claude föreslog istället `query`,
som betyder "sökfråga" och är ett vanligt namn inom programmering för det användaren söker efter. Jag behöll det
eftersom namnet säger vad variabeln används till.

Jag hade inte heller tänkt på att sökningen måste fungera oavsett stora och små bokstäver. Lösningen är att göra om
både söktexten och bokens titel och författare till små bokstäver med `toLowerCase()` innan de jämförs. Böckerna ändras
inte, det är bara en kopia som används vid jämförelsen.

Att ha bra metod- och variabelnamn var något jag fick genomgående hjälp med under programmets utveckling. På detta sätt
behöver jag färre kommentarer som beskriver vad som händer i varje kodstycke och koden är mycket mer läsbar.

### Hjälp av AI
Under arbetets gång använde jag Claude som hjälpmedel. Mitt första steg var att skicka in kravspecifikationen för att
få en lättare beskrivning av vad som skulle användas och inte. Därefter skrev vi pseudokod tillsammans för att få en skiss
på vad vi ville att koden skulle göra, vilka metoder som krävs och hur strukturen skulle se ut.

Under tiden jag utvecklade så kunde jag skicka in en fråga till Claude om hur jag skulle ta mig an ett problem när jag
körde fast. Jag fick då hjälp genom korta små frågor som jag kunde fundera på för att sedan komma fram till grundproblemet
och hur det skulle kunna lösas.

Kort sagt har jag använt Claude som ett bollplank för idéer som växt fram under tiden eller när jag inte riktigt kunde
lösa problemet helt på egen hand.

### Dynamisk kapacitet

#### Problemet
En vanlig array har ett bestämt antal platser och kan inte ändras efter att arrayen har skapats.
Det gäller både `members`- och `books`-arrayen. Detta kan leda till en full array
snabbt och de nya böckerna som fylls på får ingen plats i minnet.

#### Lösningen
I mitt program har jag valt att dubblera storleken med raderna: `Book[] bigger = new Book[books.length * 2];`
och `int newSize = members.length * 2;`. Den fasta arrayen glöms bort genom att jag pekar på
den större arrayen och Java städar bort den gamla automatiskt.
Innan detta steg händer kopierar jag den gamla arrayen och skickar in alla element i den nya arrayen.
Jag dubblerar storleken istället för att öka med en plats i taget, eftersom arrayen då räcker längre
och kopieringen inte behöver göras varje gång en bok eller medlem läggs till.
`borrowedBy` behöver även växa parallellt för att programmet ska kunna peka rätt för members, books och om den är tillgänglig.
En viktig del i funktionen är att sätta de nya platserna till `-1` för att visa att boken
är tillgänglig eftersom ingen medlem har id -1 med raden `newBorrowedBy[i] = -1;`.
Java sätter annars 0 som default på de nya platserna vilket hade kunnat leda till att en bok ser
utlånad ut till en medlem med id 0.
Jag startade med små arrayer för att visa att växandet fungerar direkt, eftersom exempeldatan fyller dem redan från start.

### Statistik
Menyval 7 visar den medlem som har lånat flest böcker just nu.
I metoden `showTopBorrower()` så använder jag en for-loop för att hitta medlemmen med flest
lån, utan Streams eller Collections. Jag börjar med att programmet kommer ihåg den första medlemmen,
`members[0]`. Därefter går loopen igenom resten av medlemmarna och kollar ifall det är någon som har
fler lån. Om det är det så kommer programmet ihåg den medlemmen istället.
Om två har samma antal lån så byts de ej ut, utan den som är först i listan visas. Det beror på att
jag använder `>` (större än), och vid samma antal lån är den nya inte större.
Specialfallen i programmet är om det inte finns några medlemmar så skickas ett meddelande till
användaren som felhantering.
Nästa fall är om ingen har lånat så pekar inte programmet ut någon medlem, här skrivs istället
ett meddelande att ingen medlem har något aktivt lån just nu.

### Sortering
Menyval 6 visar alla böcker. I `sortBooksByTitle()` använder jag en selection sort istället för `Arrays.sort()` som visar
böckerna i bokstavsordning efter titel. Den går igenom varje bok och ser vilken
titel som kommer först i alfabetet. Den byter plats med den nuvarande boken som står på plats 1
och sedan går vidare till plats 2 där den sorterar de kvarstående böckerna som inte hanterats.
Metoden fortsätter tills alla böcker är på rätt plats. `compareToIgnoreCase` gör att datorn inte bryr sig om stora och små
bokstäver. Annars sorterar datorn alla stora bokstäver framför de små.
Ett problem med detta är att eftersom jag byter plats på böckerna krävs det att lånen också hänger med
så att det inte ser ut som att fel bok är utlånad.
`swapBooks` är en hjälpmetod som byter plats i `books` och `borrowedBy` samtidigt. Att böckerna byter
plats gör ingen skillnad i menyvalet "Return book" och i "Borrow book" eftersom jag söker upp böckerna med hjälp av ISBN.

## Om Collections Framework hade varit tillåtet
Hade Collections Framework varit tillåtet hade programmet varit mindre komplext.
Min första uppgradering hade varit att använda `ArrayList<Book>` och `ArrayList<Member>` (vilket
också kräver Generics, som inte heller var tillåtet). Då hade metoderna `growBooks` och `growMembers`
och räknarna `bookCount` och `memberCount` försvunnit, eftersom listan hade vuxit när det krävdes.
Jag hade även använt en `HashMap` istället för `borrowedBy`. Där skulle jag koppla ISBN till
medlems-id, så att lånet hör ihop med boken istället för en plats i arrayen. Då hade inte heller
`swapBooks` behövts. I sorteringen hade jag valt en färdig sortering som redan finns i Java, eftersom
mer "onödig" kod ofta kan leda till komplexa buggar. I statistiken hade jag använt Streams, där det
finns färdiga lösningar för att hitta största värdet utan att skriva loopen själv.

Begränsningarna gav mig en förståelse för vad en `ArrayList` gör i bakgrunden, som jag inte visste
om innan. Jag fick själv tänka igenom vilka metoder som behövdes och hur de hänger ihop. Det gav mig
också en lärdom om hur viktiga metodnamn är för att beskriva vad koden faktiskt gör, utan att
överkommentera koden, vilket har varit ett problem för mig i tidigare utveckling. Det gav mig även
en insikt kring hur jag använder AI. Tidigare fick jag färdiga förslag på metoder utan att riktigt
veta vad till exempel en `ArrayList` egentligen gör i bakgrunden. Den här gången krävdes det att jag
förstod metoderna för att kunna koppla ihop dem.


 