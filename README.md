# Web bán hàng bách hóa

## Mô tả
**Grocery-store** là một ứng dụng **web** được thiết kế để **mô tả các chức năng cơ bản của 1 hệ thống thương mại điện tử**. Ứng dụng này tích hợp các thuật toán cơ bản để đề xuất sản phẩm dựa trên hành vi người dùng và nội dung của sản phẩm nhằm **cải thiện trải nghiệm và tìm hiểu về hệ thống thông minh, ...**.

## Công nghệ sử dụng
- **Frontend**: React, Redux, Tailwind CSS
- **Backend**: Spring Boot, MySQL
- **Authentication**: JWT, Google Oauth
- **Intelligent System**: Python x Sentence-BERT, KNN, Cosine similarity

## Cài đặt và chạy dự án
1. Clone repo:
   ```bash
   git clone https://github.com/hungp03/grocery-store
   cd grocery-store

2. Cài đặt dependencies:
   ```bash
   # Frontend
   cd client
   npm install

   # Backend
   cd server
   # load các dependencies qua pom.xml

   # model đề xuất
   # Tạo môi trường ảo
   python -m venv <tên môi trường>
   <tên môi trường>/Scripts/activate
   pip install -r requirements.txt
3. Cấu hình .env và application.yml

4. Chạy ứng dụng
