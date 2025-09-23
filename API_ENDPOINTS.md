# Movie API Endpoints

## Base URL
`http://localhost:8080/api`

## Movie Endpoints

### Get All Movies (Paginated)
```
GET /movies?page=0&size=20&sortBy=id&sortDirection=ASC
```

### Get Movie by ID
```
GET /movies/{id}
```

### Search Movies by Title
```
GET /movies/search?title={searchTerm}&page=0&size=20
```

### Get Movies by Genre
```
GET /movies/genre/{genreId}?page=0&size=20
```

### Get Movies by Year
```
GET /movies/year/{year}?page=0&size=20
```

### Get Movies by Date Range
```
GET /movies/date-range?startDate=2020-01-01&endDate=2024-12-31&page=0&size=20
```

### Create Movie
```
POST /movies
Content-Type: application/json

{
  "title": "Movie Title",
  "overview": "Movie description...",
  "posterPath": "/path/to/poster.jpg",
  "backdropPath": "/path/to/backdrop.jpg",
  "voteAverage": 7.5,
  "voteCount": 1000,
  "releaseDate": "2024-01-15",
  "runtime": 120,
  "status": "Released",
  "popularity": 85.5,
  "originalLanguage": "en",
  "originalTitle": "Original Title",
  "tmdbId": 12345,
  "genreIds": [28, 35]
}
```

### Update Movie
```
PUT /movies/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "overview": "Updated description...",
  "posterPath": "/path/to/poster.jpg",
  "backdropPath": "/path/to/backdrop.jpg",
  "voteAverage": 8.0,
  "voteCount": 1500,
  "releaseDate": "2024-01-15",
  "runtime": 125,
  "status": "Released",
  "popularity": 90.0,
  "originalLanguage": "en",
  "originalTitle": "Original Title",
  "tmdbId": 12345,
  "genreIds": [28, 35, 18]
}
```

### Delete Movie
```
DELETE /movies/{id}
```

## Genre Endpoints

### Get All Genres
```
GET /genres
```

### Get Genre by ID
```
GET /genres/{id}
```

## Pagination Parameters
- `page`: Page number (starts at 0)
- `size`: Number of items per page (default: 20)
- `sortBy`: Field to sort by (id, title, releaseDate, voteAverage, popularity)
- `sortDirection`: ASC or DESC

## Response Format for Paginated Results
```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 100,
  "totalPages": 5,
  "last": false,
  "first": true,
  "empty": false,
  "numberOfElements": 20
}
```