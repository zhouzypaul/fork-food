cs0320 Term Project 2021

### Project Description

**fork**, for those fork-in-the-road moments. it’s Tinder but for food.

Join a group or go it solo, create an account and log in to fork™. Based on individual and aggregated group preferences, we’ll find restaurants within your area and within your budget. Swipe left or right to reject a restaurant or give it an upvote, and based on the group’s decisions, we’ll pick a winner for the perfect breakfast, lunch, brunch, dinner, supper, or any other meal/snack/food consumption event.

### Team Members: 

Alan Gu, Edward Xing, Sean Zhan, Zhiyuan "Paul" Zhou

### Technical explanation of the project

Our frontend consists of 2 main parts:
1. a food preference survey 
2. a group swiping session where a top restaurant will be suggested to all group members.

Our backend consists of 4 main parts: 
1. a recommendation algorithm that aggregates a group's preference and suggests 10 restaurants 
2. a ranking algorithm that takes all group members' swiping decisions and produces a top restaurant 
3. a socket implementation that keeps track of groups and swiping sessions 
4. an API that allows for communication between the frontend, the algorithms and sockets, and the two databases (restaurants database and users database)

User authentication is implemented by keeping users' usernames and hashed passwords in the **users.sqlite3/login** table.

After successful login for the first time, users will be required to take a short survey of their preferences. 
- Users will be asked to pick what types of food they like.
- Users will be asked what price range they are comfortable with.
- Users will be asked how far they are willing to go to eat.

Their preferences will be stored into **users.sqlite3/training** table. Users can change their survey preferences anytime. 

On the home page, users can either host or join a room. This is implemented with sockets, and the socket knows which users are in which room. Note that rooms are shared with a unique 4-digit code.

Once the host starts a swiping session, the socket communicates with the recommendation algorithm, which takes all group members' preference data from **users.sqlite3/training** table, performs naive bayes classification, and talks to the socket again to send 10 recommended restaurants to all users in the group. 

The restaurants are presented to users one by one, and each user can say yes or no to each restaurant. Their decisions are stored in **users.sqlite3/training** table, which means that as users interact with the app more, the recommended restaurants will be closer to what users actually like as the recommendation algorithm has more data to analyze.

At the end of a swiping session, all users' decisions for each restaurant are sent to the ranking algorithm. The ranking algorithm will produce a weighted popular vote. The weight is decided by the number of times a user has been suggested a top restaurant that they have said yes to during recent swiping sessions. Users who have gotten their way frequently will have less say in the final decision. In this way, the ranking algorithm does its best to keep the final decision fair for all group members.

There are some extra features as well. At the end of a swiping session, the frontend offers users options to locate the top restaurant via google maps or learn more about the restaurant via a link to google search results. Users will also be able to see a list of their recent top restaurants in case they want to revisit great eateries.

### Data Preparation

We downloaded restaurants data in json format from Yelp, and we used Yelp's script to turn the json file into a csv file, then we used a python package csv-to-sqlite to convert the csv file into a sqlite file.

Dataset is downloaded from:

https://www.yelp.com/dataset

Script to transform the json file (each line represents a json object) into a csv file:

https://github.com/Yelp/dataset-examples/issues/43

csv file to sqlite file:

https://pypi.org/project/csv-to-sqlite/

#### SQL Commands to Preprocess Data

```
// keep only restaurants data
DELETE FROM yelp_academic_dataset_business AS bus WHERE
bus.categories NOT LIKE '%Food%' OR
bus.categories NOT LIKE '%Restaurants%'

// in DB Browser, attach reviews.sqlite3 to yelp_academic_dataset_business.sqlite3
// then create a table in yelp_academic_dataset called reviews containing all business reviews
INSERT INTO reviews SELECT * FROM review.yelp_academic_dataset_review

// detach reviews.sqlite3, then
// create another table that has another column numReviews representing
// the number of reviews for each restaurant
CREATE TABLE restaurants AS SELECT * FROM (
SELECT COUNT(reviews.stars) as numReviews, rest.business_id as rev_id
FROM reviews, yelp_academic_dataset_business AS rest
WHERE reviews.business_id = rest.business_id GROUP BY rest.business_id 
) AS new INNER JOIN yelp_academic_dataset_business
ON rev_id = yelp_academic_dataset_business.business_id
```

### API Documentation

#### restaurants

- ```/getRestByID``` body format: ```{"id": rest_id}```, returns restaurant with ```rest_id``` in format ```{"name": rest_name, "numStars": stars, "numReviews": num_reviews, ...}``` For the specific format, please refer to ```database/queries/QueryRestaurants/getRestaurantsWithPrep```.

- ```/getRestByRad``` body format: ```{“radius": rad, "lat": center_lat, "lon": center_lon}```, returns restaurants within the square bounding box with side length ```2*rad``` around the center coordinate.

- ```/getMostRecentRests``` body format: ```{"username": user_id}```, returns user's 10 most recent top restaurants in newer to older order, return format: ```{"restaurants": [map, map, ..], "timestamps": [string, string, ...], "err": err_msg}```

- ```/deleteMostRecentRests``` body format: ```{"username": user_id}```, delete all of user's most recent top restaurants, return format: ```{"success": succ, "err": err_msg}``` 

#### user

- ```/getAllUserIds``` no required body, returns all ```userId```s.

- ```/login``` body format: ```{"username": user_id, "password": new_pwd}```, returns whether the password is correct

- ```/register``` body format: ```{"username": user_id, "password": user_pwd}```, returns whether the action is successful

- ```/deleteUser``` body format: ```{"username": user_id}```, returns whether the action is successful

- ```/updatePwd``` body format: ```{"username": user_id, "password": new_pwd}```, returns whether the action is successful

- ```/getUserPwd``` body format: ```{"username": user_id}```, returns user's password in format ```{"pwd": user_pwd}```

- ```/getUserPref``` gets user's survey response from /user/training table. body format: ```{"username": user_id}```, returns user with ```user_id``` in format ```{"types": [], "prices": [], "radius": "", "err": ""}```

- ```/updateUserPref``` updates user's survey response in /user/training table. body format: ```{"username": user_id, "types": arr_of_food_types, "price": arr_of_preferred_price_ranges, "radius": preferred_radius}```

- ```/insertUserPref``` inserts user's swiping responses into /user/training table. body format: ```{"username": user_id, "latitude": lat, "longitude": lon, "business_id_arr", arr_of_recommended_restaurants, "swipe_decision_arr", arr_of_1s_and_0s}``` 

### Algorithms

#### Recommendation Algorithm

[TODO] some more detailed explanation than "Techincal explanation of the project"

#### Ranking Algorithm

[TODO] some more detailed explanation than "Techincal explanation of the project"

### Testing

This project has thoroughly tested the two algorithms and various backend methods. The socket implementation, the handlers, and the frontend are tested by system tests (interacting with the frontend).

Here are some of the system tests we have conducted to make sure users have a great experience:

- Trying to sign up with existing username
- Trying to sign in with a deleted account
- Not selecting anything when prompted the preferences survey.
- Selecting some food types but not price ranges.
- Selecting price ranges but not food types.
- Not swiping right on any of the suggested restaurants. 

  
### How To Run

```
// compiles project
mvn package

// starts the backend, databases will be automatically loaded
./run

// load custom databases
load rest path_to_custom_restaurants_db
load user path_to_custom_user_db
```

### Team Strengths and Weaknesses:

Alan Gu:

- Strength: OOP, Javadocs, JS, SQL

- Weakness: CSS centering

Ed Xing:

- Strength: Project Design and Conceptualization

- Weakness: Javascript

Sean Zhan:

- Strength: Figuring out how different components fit together, React

- Weakness: CSS, HTML

Paul Zhou:

- Strength: Algorithms

- Weakness: Anything related to frontend


## Project Idea(s): 

### Idea 1: Cliq (Decision Making App)

**Problem**: Coming up with and agreeing on ideas in group outings.

**Solution**: Each member of the group has an account and he/she is presented with ideas based on their preferences. Each person clicks the ones they like one by one and the one with the most matches is chosen. The generated ideas could pertain to movies, restaurants, and attractions.

**Features**:

- Recommendation algorithm
  
    - Allows personalized ideas that users are interested in
Technically difficult to implement as there are many factors to consider in making good decisions
From the user’s point of view, this feature makes the app a tool they would likely use since it would be effective at generating ideas that they would be excited for without having to think themselves

- Multiple accounts

    - Recommendations could be personalized and decisions can be made anonymously
Requires multithreading and a database
Must be safe and private so that sensitive data is not leaked
Allows for a personalized experience that is both social and fruitful

- Chat

    - To facilitate communication amongst members of a circle
Requires networking and is generally unfamiliar territory
Allows for interaction and conversation between friends, necessary for planning and maintaining social interactions

**HTA Approval (dpark20):** Idea approved; make sure the algorithm is more complex than just adding up scores for each activity option based on each ranking. Perhaps take into account what/when needs to be weighed more heavily, and for which users! Be sure to add something that makes this project unique.

### Idea 2: 

**Problem**: It often happens that we are interested in some field and gain more experience in that field (software engineering, for example). However, it is hard to come up with a project to work on, or find people who have similar interests as you and want to work on a project together.

**Solution**: We bring together a platform where people can find project opportunities related to their interests. The users can either 1) post opportunities about a project they are passionate about and look for teammates or 2) browse the posted opportunities and find someone to work with.

**Features**:

- Recommendation system

    - It is super important that the platform recommends contents related to each user’s individual interests. This is what keeps our user interested and active.

    - This would require a great recommendation algorithm. Without the power of big data and big learning, it is hard to find a perfect recommendation algorithm. However, we can still create great recommendations using simple NLP techniques (such as turning natural language to vectors and comparing the cosine similarity). We could also use the Hungarian Algorithm to perform bipartite graph matching to match each user with a potential similar user who have similar interests.

    - The users of this platform are eager to connect with others and hopefully find a project they are interested in. In reality, this feature could also be used for matching group projects in a university setting.

- Search functionality

    - Users should be able to search the contents they want to find. For example, they might be interested in searching for listed opportunities where software engineers want to create a simple google-map-like application.
    
    - A good search algorithm is needed in order to generate high quality search results. We would need to implement term frequency algorithm and page-rank algorithm.

    - The users might not want to browse through all the stuff the platform recommends for them. Instead, they want to be able to search for the exact stuff they are looking for.

- Individual Accounts

    - Users need to have individual accounts to store their interests, the projects they are interested in, and the connections they made on the website.

    - This would require the implementation of a backend database that stores the users’ info securely. This backend also needs to be able to communicate with the frontend in real time for an interactive experience.
    
    - The users want to be able to have a personalized experience on this platform, where they can have access to their past activities.

**HTA Approval (dpark20):** Idea approved (contingent). This has been done before, so make sure you are doing something unique to add more depth into the project! The account system shouldn't be the focus of your project; look to add something that gives users a greater reason to use your application.

### Idea 3: Tradr

**Problem**: I have so many unwanted items, but nothing to trade them for. Also I hate using money.

**Solution**: Tradr, the Tinder of trading. Fill your profile with items you'd like to trade, and swipe through items you'd like to trade them for.

**Features**:

- User Accounts

    - Store user accounts, mainly inventory of personal items, and item metadata

    - Perhaps user contact information, geography, other information

    - User authentication

    - Stored in backend database

- Matching and chat

    - Users are able to match based on Tinder-like swiping mechanism

        - Challenge: Representation of matching, how to store this information
    
    - Implement a chat feature

- Algorithm: Item similarity, Recommendation

    - Match items based on similar price range, anything else?

        - Clustering algorithm on item types?

        - Group items by category and allow user specified search

    - Match users with items based on preferences as determined by recommendation algorithm? classification algorithm

**HTA Approval (dpark20):** Idea approved (contingent). Like before, this has been done before, so make sure you are doing something unique to add more depth into the project!



Mentor TA: Put your mentor TA's name and email here once you're assigned one!

Meetings
On your first meeting with your mentor TA, you should plan dates for at least the following meetings:

Specs, Mockup, and Design Meeting: (Schedule for on or before March 15)

4-Way Checkpoint: (Schedule for on or before April 5)

Adversary Checkpoint: (Schedule for on or before April 12 once you are assigned an adversary TA)

How to Build and Run
A necessary part of any README!
