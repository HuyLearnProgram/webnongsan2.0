import path from "./path";
import icons from "./icons";
export const navigation = [
  {
    id: 1,
    value: "Trang chủ",
    path: `/${path.HOME}`,
  },
  {
    id: 2,
    value: "Sản phẩm",
    path: `/${path.PRODUCTS}`,
  },
  {
    id: 3,
    value: "Dành cho bạn",
    path: `/${path.FOR_YOU}`,
  }
];
const { FaTruck, GiReturnArrow, SiAdguard, FaPhone } = icons

export const productExtraInfo = [{
  id: 1,
  title: 'Sản phẩm sạch',
  sub: 'Cam kết không chất bảo quản',
  icon: <SiAdguard />
},
{
  id: 2,
  title: 'Vận chuyển siêu tốc',
  sub: 'Đảm bảo nhận hàng trong 1 giờ với bán kính < 10km',
  icon: <FaTruck />
},
{
  id: 3,
  title: 'Hoàn trả miễn phí',
  sub: 'Trả hàng miễn phí khi có vấn đề phát sinh',
  icon: < GiReturnArrow />
},
{
  id: 4,
  title: 'Hỗ trợ 24/7',
  sub: 'Hoạt động liên tục kể cả thứ 7, CN',
  icon: <FaPhone />
}
]
export const voteOption = [
  {
    id: 1,
    text:"Rất tệ"
  },
  {
    id: 2,
    text:"Tệ"
  },
  {
    id: 3,
    text:"Bình thường"
  },
  {
    id: 4,
    text:"Tốt"
  },
  {
    id: 5,
    text:"Rất tốt"
  }
]

export const ratingStar = [
  '1','2','3','4','5'
]

