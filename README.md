# Evidence

This tool helps keeping in touch with the latest personal progress made throughout watching different kind of TV shows, such as Animes, TV Series, Movies, and so on. Its usage is straightforward, as it helps the user to easily understand its functionalities.

![alt tag](http://i64.tinypic.com/1imkq0.jpg)

The option to add and remove a so called entity are in the upper left side of the window. Right below, there are presented the current entities, our case Animes, alongside their details in the right. 

|**Details**            |
|:----------------------|
| name                  |
| status                |
| rating                |
| start date            |
| end date              |
| season                |
| current episode       |

Editing an entity name is possible by using the text field just in the right of the `Name` field. Either if the operation succeeds or not, talking in database terms, the text field will change its background color accordingly, therefore `green for success` and `red for failure`.

Increasing and decreasing the number of seasons / episodes watched is facilited by `<` respectively `>` buttons. A season value cannot be lesser than 1, and an episode lesser than 0. Also, a specific number of a season / episode can be inserted directly in the text field right between the increasing and decreasing buttons, followed by the pressing of the ENTER key. This will cause its saving.

We are able to give a rating by clicking on the stars accordingly, either by manually typing the rating in the text field right below.
The progress made by watching can be modified by the `<` and `>` buttons on the left and respectively on the right on the text field that represents the number of the current episode. Also, a number can be manually typed in the text field itself.

Also, an entity can be marked as finished (or unfinished, as the button converts accordingly its name and function), as the user finished watching it, by clicking on the `Mark as finished` button. That makes the following  facilities prohibited:

| **Usage prohibitions**                                                                                 |
| :------------------------------------------------------------------------------------------------|
| name text field                                                                                  |
| status combobox                                                                                  |
| `<` and `>` buttons (for both 'Season' and 'Current episode')                                    |
| current episode text field                                                                       |
| `rating` functionality (stars + text field)                                                      |
| note: the `Mark as finished` button becomes `Mark as unfinished` with the specific functionality |

The tool also gives the opportunity to add or delete a category by `Add new category` and `Delete` buttons right in the upper right of the window. In the same place we are also able to check the categories. Currently we are seeing the Anime category.

The search bar allows us to easier find an entity by simply typing its name inside it. The search goes as follows:
* firstly, it looks for the actual entity
* secondly, if the first condition is not met, it looks for entities that contain the search value
* if the search returns no results at all, the search bar background will get colored to red

If the second phase takes place, after the entities are found, they are retrieved in a combo box just in the right of the search bar, so the user have all the entities that match that search result, as in the print below:

![alt tag](http://i67.tinypic.com/20zekqg.jpg)
```
All these details are saved in a database.db file (SQLITE) which is automatically created (if it does not already exist) in the current folder of the Evidence.jar executable.
```
