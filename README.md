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

```sqlite
// keep only restaurants data
DELETE FROM yelp_academic_dataset_business AS bus WHERE
bus.categories NOT LIKE '%Food%' OR
bus.categories NOT LIKE '%Restaurants%';

// in DB Browser, attach reviews.sqlite3 to yelp_academic_dataset_business.sqlite3
// then create a table in yelp_academic_dataset called reviews containing all business reviews
INSERT INTO reviews SELECT * FROM review.yelp_academic_dataset_review;

// detach reviews.sqlite3, then
// create another table that has another column numReviews representing
// the number of reviews for each restaurant
CREATE TABLE restaurants AS SELECT * FROM (
SELECT COUNT(reviews.stars) as numReviews, rest.business_id as rev_id
FROM reviews, yelp_academic_dataset_business AS rest
WHERE reviews.business_id = rest.business_id GROUP BY rest.business_id 
) AS new INNER JOIN yelp_academic_dataset_business
ON rev_id = yelp_academic_dataset_business.business_id;
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

This projects hosts an easily extensible recommendation package. The currently in use recommendation
algorithm is a Naive Bayes Classifier. The Naive Bayes classifier uses the bayes rule is statistics to
compute the posterior probability. Given the attributes of a resutarant, the classifier will be able to
output the probability that the user likes the restaurant. It is implemented together with Laplace smoothing
to handle restaurant attributes that's not in the training data.

#### Ranking Algorithm

The ranking algorithm is a weighted average of the votes of each user on each of the 10 recommended restaurants.
But in order to make the process fairer for every users, the ranking is a weighted average, taking into
account the number of times the ranking algorithm picked something the user liked in the past. This is accomplished
by keeping record of a `gottenWay` variable that's between 0 and 1, representing the weight of each person's vote (the
larger the heavier the weight), and is updated on each run of the ranking algorithm.

### Testing

TO run unit tests, run
```bash
mvn package
mvn test
```

This project has thoroughly tested the two algorithms and various backend methods. The socket implementation, the handlers, and the frontend are tested by system tests (interacting with the frontend).

Here are some of the system tests we have conducted to make sure users have a great experience:

- Trying to sign up with existing username
- Trying to sign in with a deleted account
- Not selecting anything when prompted the preferences survey.
- Selecting some food types but not price ranges.
- Selecting price ranges but not food types.
- Not swiping right on any of the suggested restaurants. 
- Trying to brute force to certain pages using URLs without signing in.
- Trying to brute force to room with room code without starting a session.
- Host leaving the room before starting.
- Member leaving the room before starting.
- Member leaving the room after starting.

  
### How To Run

```bash
// compiles project
mvn package

// starts the backend, databases will be automatically loaded
./run

// starts the frontend
cd fork-react
npm install
npm run start
```
