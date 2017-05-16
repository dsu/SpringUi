SPRINGUI
========

Components based framework on Spring Boot. Contains Shredder liblary for logging into H2 database.

DONE
====

* Przykładowy komponent typu strona (TPage)
* Komponenty do includowania JS/CSS poprzez adnotacje + "odświeżanie" cache JS (dodatkowy parametr w ścieżce) - częsty przypadek że klientowi nie działa, zaoszczedzi parę godzin w roku.
* Engine do template w XSL/THYMELEAF/FREEMAKER/JAVA
* Przykład listy opartej na adnotacjach
* Przechowywanie "drzewa" komponentów w sesji, przekazywanie id widoku przez cookie i zwracanie go ponownie przy Ajaxie. Drzewo przechowujm w sesji max n drzew - możliwe wystąpienie błędu o expired view, zresztą jak w JSF.
 
* Przykład profilera z użyciem aspektów (w praktyce potrafi dodawać losowe milisekundy) i adnotacji @Profile.
* Przykład konfiguracji beana z użyciem customowych parametrów w pliku .properties Spring Boot.
* Przykład keszowanej metody (renderowanie w komponencie przykładowej strony) na podstawie session id oraz parametrów. Keszowanie zależne od np. parametrów, sesji lub tylko jeżeli np. brak jaichkolwiek parametrów w requeście (eg. @Cacheable(cacheNames = "pages", keyGenerator = "uiComponentKey", unless = "@tpage.hasRequestParameters()")).
* Project Lombok - do np. generowania equals i hashCode @EqualsAndHashCode w Komponentach - raczej wymagane.
* Biblioteka do logowania do bazy H2 wraz z dzienną archiwizacją i podglądem danych (servelt podczepiony poprzez konfigurację Spring boot)



Zadania encji w systemie:
=========================

 Drzewo komponetów
-----------------
 * Nadawanie identyfikatorów komponentom
 * Przechowywanie stanów komponentów 

Kontekst
---------
 * Przekazywanie danych z sesji
 * Przekazywanie danych z odpowiedzi do komponentów
 
 
Beany/Kontrolery
------

 * Odczytywanie danych z komponentów - lepiej bezpośrednie przekazywanie do nich danych z komponentów
 * Przechowywanie danych dla komponentów? 

Komponenty/Widoki
----------

 * Przechowywanie swojego własnego stanu.
 * Renderowanie samego siebie.
 * Opcjonalnie wygenerowanie JS do wykonania po stronie klienta - ale nie gdzieś w template ukradkiem, tylko jawnie - osobna metoda interfejsu.
 * Jeden komponent może działać samodzielnie do renderowania całej strony
 * Problem - komponenty renderowane na serwerze, osobno mają małe możlwiości, słabo się to skaluje. 
   Renderowanie danych z serwera (Drzewo w JSON) dało by dużo większe możliwości, wydajność. 
 * Komponent typu Json - renderuje sam z siebie Jsona, w osobnym pliku JS kod komponentu od strony klienta.
 * Rejestrowanie komponetów w drzewie komponentów
 * Nie można przechowywać żadnych metadanych w szablonach - np. o strukturze drzewa komponentów w szablonie więc trzeba tym zarządzać samodzielnie w komponencie.
  
  

 
 