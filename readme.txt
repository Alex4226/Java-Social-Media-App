========================================================================
LABORATOR 5 - MAP
Retea de Socializare - Persistenta Relationala (PostgreSQL & JDBC)
========================================================================

1. DESCRIEREA ARHITECTURII SI A CLASELOR
------------------------------------------------------------------------
- User, Friendship, Tuple: Reprezinta entitatile din pachetul domain.
  User modeleaza utilizatorul retelei (cu ID generat automat de baza de date
  la persistare), Tuple modeleaza o pereche generica de identificatori ordonati
  (min, max), iar Friendship retine ID-ul compus si data crearii relatiei.

- CriteriiUtilizator: Clasa utilitara ce expune predicate statice compozabile
  (byLastNamePrefix, byEmailDomain, faraPrieteni, cuPrieteni) folosite pentru
  filtrarea flexibila a utilizatorilor pe baza java.util.function.Predicate.

- Repository<ID, E>: Interfata generica de baza ce defineste contractul CRUD
  minimal (find, getAll, add, delete, update) imbogatita cu metoda default
  findAll(Predicate<E>) bazata pe Stream API.

- InMemoryRepository<ID, E> & AbstractFileRepository<ID, E>: Implementari
  concrete in memorie si pe fisiere text (Java NIO), pastrate intacte si
  interchangeabile prin polimorfism.

- UserDbRepository, FriendshipDbRepository: Implementari concrete ale
  contractului Repository<ID, E> peste baza de date relationala PostgreSQL,
  utilizand JDBC (DriverManager, PreparedStatement, ResultSet).

- RepositoryFactory: Instantiator polimorfic configurat functional prin strategii
  Map<String, Function<...>>, care citeste 'repository.type' si parametrii de
  conectare (db.url, db.user, db.password) din 'config.properties'.

- RepositoryException: Exceptie necontrolata a domeniului, utilizata pentru
  impachetarea si decuplarea erorilor JDBC (SQLException) si de I/O de
  straturile superioare (exception chaining).

- Validator<T>: Interfata functionala (@FunctionalInterface) ce defineste metoda
  validate(T) si metoda default andThen(Validator<T>) pentru compunerea regulilor.

- SocialNetworkService: Stratul de business logic; delega generarea ID-urilor
  catre stratul de stocare (ID null la creare), asigura consistenta relatiilor
  si executa analizele pe graf folosind Stream API si Optional.

- ConsoleUI: Interfata text cu utilizatorul; afiseaza ID-ul generat de baza de
  date la inregistrarea utilizatorilor si trateaza explicit exceptiile de
  validare, persistenta sau conexiune indisponibila fara stack trace.


2. SCHEMA BAZEI DE DATE (POSTGRESQL - 'socialnetwork')
------------------------------------------------------------------------
- Tabela 'users':
  users(
      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      first_name VARCHAR NOT NULL,
      last_name VARCHAR NOT NULL
  )
  * Nota: ID-ul este generat exclusiv de PostgreSQL prin coloana IDENTITY si
    recuperat la inserare prin Statement.RETURN_GENERATED_KEYS / RETURNING id.

- Tabela 'friendships':
  friendships(
      id1 BIGINT NOT NULL,
      id2 BIGINT NOT NULL,
      friends_from TIMESTAMP NOT NULL,
      PRIMARY KEY (id1, id2),
      CHECK (id1 < id2),
      FOREIGN KEY (id1) REFERENCES users(id),
      FOREIGN KEY (id2) REFERENCES users(id)
  )
  * Nota: Ordinea canonica (id1 < id2) previne dublarea relatiilor simetrice
    la nivel de baza de date, dubland normalizarea din clasa Tuple.


3. IMPLEMENTARE JDBC, SECURITATE SI CONTRACT REPOSITORY
------------------------------------------------------------------------
a) Gestionarea resurselor si conectivitatea:
   - Conexiunile sunt deschise prin DriverManager.getConnection(url, user, password)
     cu parametrii injectati din RepositoryFactory.
   - Toate resursele JDBC (Connection, PreparedStatement, ResultSet) sunt
     declarate in blocuri try-with-resources pentru inchidere garantata.

b) Prevenirea SQL Injection:
   - Toate interogarile SQL (INSERT, SELECT, UPDATE, DELETE) folosesc exclusiv
     PreparedStatement cu parametri pozitionali ('?').
   - Valorile introduse de utilizator nu sunt concatenate niciodata in textul
     interogarii, tratand secvente precum "'); DROP TABLE users; --" ca simple
     siruri de caractere.

c) Respectarea contractului Repository<ID, E>:
   - Argumentele null arunca IllegalArgumentException.
   - Validarea entitatilor se executa obligatoriu inainte de add (save) si update.
   - update(entity) verifica rezultatul executeUpdate(): intoarce entitatea
     primita daca ID-ul nu exista in baza de date (0 randuri afectate) si null
     daca actualizarea a reusit.
   - delete(id) recupereaza entitatea inainte de stergere si intoarce entitatea
     stearsa (sau null daca ID-ul nu exista).


4. FACTORY FUNCTIONAL SI CONFIGURARE EXTERNA
------------------------------------------------------------------------
- Comutarea intre tipurile de persistenta ('database', 'file', 'memory') se
  realizeaza exclusiv prin modificarea cheii 'repository.type' din fisierul
  'config.properties', fara recompilarea codului sursa.
- Credentialele reale sunt izolate in 'config.properties' (exclus din Git prin
  '.gitignore'), in timp ce structura asteptata este documentata in
  'config.example.properties'.
- Driverul JDBC 'org.postgresql:postgresql:42.7.7' este configurat in proiect.


5. TRATAREA EXCEPTIILOR SI DOCUMENTATIE
------------------------------------------------------------------------
- Erorile JDBC (SQLException) sunt capturate in repository-uri si re-aruncate
  incapsulate in