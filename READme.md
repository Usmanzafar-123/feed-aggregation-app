Here is a clean, professional README.md documentation for your Social Media API. This is exactly the kind of documentation you should include in your GitHub repository or send to a frontend developer/client.

📱 Social Media API Documentation
A Spring Boot-based REST API for a social media platform featuring user management, post creation, follow logic, and a paginated feed system.

🚀 Base URL
http://localhost:8080

👤 User Endpoints
1. User Signup
Create a new user account.

URL: /users/signup

Method: POST

Request Body:

JSON

{
  "username": "usman",
  "email": "usman@example.com",
  "password": "securePassword123"
}
Success Response: 201 Created

Body: UserResponse object (ID, username, email).

2. Follow User
Follow another user to see their posts in your feed.

URL: /users/{followerUsername}/follow/{followedUsername}

Method: POST

Success Response: 200 OK

Body: "Followed successfully"

📝 Post Endpoints
1. Create Post
Publish a new post to the platform.

URL: /posts/{username}/createPost

Method: POST

Request Body:

JSON

{
  "content": "Just testing my new social media API!"
}
Success Response: 201 Created

Body: PostResponse (content, username, createdAt).

2. Get All Posts
Retrieve every post in the system (mainly for admin/testing).

URL: /posts/all

Method: GET

Success Response: 200 OK

Body: List<PostResponse>

📰 Feed & Discovery
1. Get Paginated Feed
Get a personalized feed of posts from the users you follow, ordered by the latest first.

URL: /posts/feed?page=0&size=10

Method: GET

Query Parameters:

page (default: 0): The page number to retrieve.

size (default: 10): Number of posts per page.

Success Response: 200 OK

Body: List<PostResponse>

🛠 Tech Stack
Java 17+

Spring Boot 3.x

Spring Data JPA (PostgreSQL)

Lombok

Validation API