-- Tạo Database mới cho dự án Love Portfolio
CREATE DATABASE IF NOT EXISTS love_portfolio_db;
USE love_portfolio_db;

-- Lưu ý: Khi chạy Spring Boot với ddl-auto=update, 
-- các bảng sẽ tự động được tạo khi bạn khởi động ứng dụng lần đầu.

-- Cấu trúc bảng Milestone (Để tham khảo)
-- CREATE TABLE milestone (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     date VARCHAR(255),
--     title VARCHAR(255),
--     description TEXT
-- );

-- CREATE TABLE milestone_images (
--     milestone_id BIGINT,
--     images VARCHAR(255),
--     FOREIGN KEY (milestone_id) REFERENCES milestone(id)
-- );
