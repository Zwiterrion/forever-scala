## Exercice 1
Présentation de play
Créer une api qui retourne un json
* /messages/:msg
  => retourne { message: "Hello $msg" }
## Exercice 2 : CRUD part 1
CRUD avec un store en mémoire (HashMap)
Api de la forme
```scala 
def get(id: String): Option[User]
```
* Model sous forme de case class
    * User avec id, nom, prénom
* toJson fonction case class => String avec string interpolation
## Exercice 3
* Utiliser play json pour sérialiser les case class
  Enrichir l'api :

```scala
def create(id: String, data: User): Either[ValidationError, User]
def update(id: String, data: User): Either[ValidationError, User]
def delete(id: String, data: User): Unit
```
* Manipulation d'option
* Validation avec Either
    * create vérifie que le user n'existe pas
## Exercice 4
* Modifier le CRUD avec des futures
* Utiliser WS client pour appeler une api de stockage clé valeur
## Exercice 5
* CRUD +++ avec composition d'appels asynchrones
* SSE ou web socket avec streams