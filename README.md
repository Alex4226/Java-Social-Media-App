========================================================================
LABORATOR 4 - MAP: PROGRAMARE FUNCTIONALA, STREAM API SI REFACTORIZARE
========================================================================

1. TABEL COMPARATIV BUCLE INLOCUITE CU STREAM API
------------------------------------------------------------------------
| Nr | Clasa                  | Metoda                  | Inainte (Procedural)       | Dupa (Stream API / Functional)                  |
|----|------------------------|-------------------------|----------------------------|-------------------------------------------------|
| 1  | SocialNetworkService   | computeNextUserId       | for-each peste userRepo    | .map(User::getId).max().map(+1).orElse(1L)      |
| 2  | SocialNetworkService   | removeUser              | 2 bucle for (filtrare+del) | .filter().map().collect().forEach(delete)       |
| 3  | SocialNetworkService   | buildAdjacencyList      | for peste userRepo         | .collect(Collectors.toMap(User::getId, ...))    |
| 4  | SocialNetworkService   | getMostSociableCommunity| for pe componente & noduri | .stream().max(Comparator.comparingInt(...))     |
| 5  | AbstractFileRepository | loadData                | while (reader.readLine())  | Files.lines() + IntStream.range(0, size)        |
| 6  | AbstractFileRepository | writeToFile             | for-each + writer.write()  | Stream.map(this::createEntityAsString) + write  |
| 7  | ConsoleUI              | uiShowUsers / Prietenii | for-each iterare consola   | StreamSupport.stream().forEach(System.out::print)|
------------------------------------------------------------------------

2. INTERFETE FUNCTIONALE UTILIZATE
------------------------------------------------------------------------
- Validator<T> (interfata proprie adnotata @FunctionalInterface):
  Defineste contractul de validare a entitatilor cu metoda default andThen.
- Predicate<T> (java.util.function.Predicate):
  Utilizata in Repository.findAll(Predicate<E>) si in CriteriiUtilizator
  pentru filtrarea dinamica a utilizatorilor, combinata prin and, or, not.
- Function<T, R> (java.util.function.Function):
  Utilizata in RepositoryFactory ca strategie Map<String, Function<...>>
  pentru instantierea repository-urilor eliminand instructiunea switch.
- Consumer<T> (java.util.function.Consumer):
  Utilizata implicit in operatiile forEach terminale (ex: System.out::println,
  friendshipRepo::delete).
- Comparator<T> (java.util.Comparator):
  Utilizata in selectarea componentei conexe maxime:
  Comparator.comparingInt(comp -> computeLongestPathInComponent(comp, adjList)).

3. REFERINTE LA METODE (TOATE CELE 4 TIPURI)
------------------------------------------------------------------------
- Metoda statica:
  CriteriiUtilizator::faraPrieteni
  Objects::nonNull
- Metoda de instanta a unui obiect arbitrar:
  User::getId
  Long::compareTo
- Metoda de instanta a unui obiect particular:
  System.out::println
  this::createEntityAsString
  friendshipRepo::delete
- Constructor:
  InMemoryRepository::new

4. JUSTIFICARI DE DESIGN SI ARHITECTURA
------------------------------------------------------------------------
a) Decizia Optional la nivel de Repository vs Service:
Interfata Repository pastreaza returnarea valorii null in metodele CRUD de
baza (find, add, delete, update), respectand contractul canonic si evitand
overhead-ul la nivelul structurilor de stocare interne.
Optional este utilizat la nivelul stratului Service (SocialNetworkService),
impachetand rezultatele (Optional.ofNullable(repo.find(id))) pentru
a forta tratarea cazurilor de absenta cu orElse, orElseThrow si a elimina
complet NullPointerException in logica de business.

b) Pastrarea Template Method in AbstractFileRepository:
S-a optat pentru pastrarea sablonului Template Method (extractEntity si
createEntityAsString ca metode abstracte) in loc de compozitie cu Function,
deoarece fiecare subtip de repository (UserFileRepository, FriendshipFileRepository)
are o responsabilitate bine delimitata legata de schema de date a fisierului sau,
mentinand ierarhia curata si extensibila.

c) Bucle / Structuri recursive pastrate:
In algoritmul dfsLongestPathFromNode (SocialNetworkService) s-a pastrat
recursivitatea algoritmica cu backtracking (adaugare nod pe drum, explorare
si stergere nod din drum la revenire). Aceasta structura cu stare mutabila locala
este permisa expres in enunt, transformarea ei integrala fiind nenaturala
pentru semantica de backtracking.
========================================================================