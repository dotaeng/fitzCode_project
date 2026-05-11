-- search_log
CREATE TABLE IF NOT EXISTS search_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    search_keyword VARCHAR(255) NOT NULL,
    search_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(user_id)
    );

-- search_ranking_history
CREATE TABLE IF NOT EXISTS search_ranking_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    search_keyword VARCHAR(255) NOT NULL,
    ranking INT NOT NULL,
    date DATE NOT NULL,
    CONSTRAINT unique_keyword_date UNIQUE (search_keyword, date)
    );

-- user
CREATE TABLE IF NOT EXISTS USER (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    password VARCHAR(255) NOT NULL
    );