Java Backend and AWS repo for Chase.io

# What it does
A multiplayer geo tag game in which you run about the real world picking up items to gain points and dueling others to absorb their points as yours. The more points you get, the bigger you become and the larger your attack radius. Items within your attack radius will be automatically picked up. When another player enters your attack radius, you duel and the player with more points wins and steals points from the other player. We integrated Terra API heart rate data as a core mechanic in our game - if you are below a certain heart rate, you will start losing points, and when you are above a threshold heart rate you become ‘on fire’ and gain points every second. 
 #How we built it

We used Terra API to easily stream heart rate data from wearables which we use as game mechanics. A Java backend was used with Websockets to obtain JSON heart rate data from Terra API. We used a Swift frontend with Apple Maps API to get real world GPS data and to create a map around us. We used Docker with AWS to deploy the app so that we could use mobile data to play the game anywhere we wanted.

# Inspiration
We all want to exercise, but it’s boring working out alone. Covid has changed our habits, making us spend more time indoors - possibly playing games. We decided to take advantage of that by gamifying exercise and offering a fun way to train with friends. We created a multiplayer tag game which uses the world around you as a playground and is supercharged with Terra API to promote less resting and more running - because we all know resting is everyone’s favourite exercise. 
