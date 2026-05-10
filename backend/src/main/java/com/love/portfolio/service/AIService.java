package com.love.portfolio.service;

import org.springframework.stereotype.Service;

@Service
public class AIService {

    public String getAIResponse(String userPrompt) {
        String input = userPrompt.toLowerCase();

        // Logic trả lời dựa trên từ khóa (Smart Keyword Engine)
        if (input.contains("học phí") || input.contains("giá") || input.contains("bao nhiêu tiền")) {
            return "Chào bạn! Học phí các lớp Gia sư của anh Trường thường dao động từ 200k - 350k/buổi (2 giờ). Đặc biệt, nếu bạn đăng ký theo nhóm 2-3 người sẽ được giảm giá 20% đấy!";
        }
        
        if (input.contains("địa chỉ") || input.contains("ở đâu") || input.contains("hà nội")) {
            return "Anh Trường dạy trực tiếp tại khu vực Cầu Giấy và Nam Từ Liêm (Hà Nội). Nếu bạn ở xa, anh có các lớp Online chất lượng cao qua Zoom/Google Meet với bảng điện tử cực kỳ trực quan.";
        }

        if (input.contains("hóa") || input.contains("hóa học")) {
            return "Về môn Hóa thì bạn tìm đúng người rồi! Anh Trường đạt 10 điểm Hóa THPTQG và Giải Nhì HSG Tỉnh. Anh chuyên trị các ca mất gốc, giúp học sinh bứt phá lên 8+, 9+ bằng phương pháp 'Dồn chất' và 'Bảo toàn' cực hay.";
        }

        if (input.contains("lịch học") || input.contains("rảnh") || input.contains("đăng ký")) {
            return "Hiện tại anh Trường còn trống một vài buổi tối trong tuần. Bạn có thể xem chi tiết tại mục 'Lịch rảnh' trên trang Hồ sơ Gia sư hoặc để lại số điện thoại, anh sẽ gọi lại tư vấn ngay!";
        }

        if (input.contains("toán") || input.contains("lý")) {
            return "Ngoài môn Hóa, anh Trường cũng nhận dạy kèm Toán và Lý cấp 2, cấp 3. Anh tập trung vào tư duy giải nhanh bằng Casio và hiểu bản chất hiện tượng để học sinh không bị học vẹt.";
        }

        if (input.contains("dự án") || input.contains("lập trình") || input.contains("project")) {
            return "Anh Trường là sinh viên UET nên rất đam mê lập trình. Các dự án của anh ấy bao gồm Hệ thống quản lý lớp học, Website đấu giá, và chính trang Web bạn đang xem đây! Bạn có thể xem chi tiết ở mục 'Dự án'.";
        }

        if (input.contains("hello") || input.contains("chào") || input.contains("hi")) {
            return "Chào bạn! Mình là trợ lý ảo của anh Trường. Bạn cần mình tư vấn về học tập, lịch học hay muốn tìm hiểu thêm về các dự án lập trình của anh Trường?";
        }

        // Câu trả lời mặc định nếu không khớp từ khóa
        return "Cảm ơn bạn đã nhắn tin! Câu hỏi này mình sẽ chuyển trực tiếp cho anh Trường. Bạn vui lòng để lại số điện thoại hoặc nhắn tin qua Facebook 'Mtruongdayyy' để anh phản hồi nhanh nhất nhé.";
    }
}
