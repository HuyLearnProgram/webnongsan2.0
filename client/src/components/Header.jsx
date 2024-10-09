import React, { useEffect, useState } from "react";
import logo from "../assets/logo.png";
import icons from "../utils/icons";
import { Link } from "react-router-dom";
import path from "../utils/path";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../store/user/userSlice";

const {FaUserCircle, FaCartShopping } = icons;

const Header = () => {
  const {current} = useSelector(state => state.user)
  const [isShowOption, setIsShowOption] = useState(false)
  const dispatch = useDispatch()
  console.log(current);
  useEffect(()=>{
    const handleClickOutOption = (e)=>{
      const profile = document.getElementById('profile')
      if(!profile.contains(e.target)) setIsShowOption(false)
    }
    document.addEventListener('click',handleClickOutOption)
    return ()=>{
      document.removeEventListener('click',handleClickOutOption)
    }
  },[])
  return (
    <div className="flex justify-between w-main h-[110px] py-[35px]">

      <Link to={`/${path.HOME}`}>
        <img src={logo} alt="logo" className="w-[120px] object-contain" />
      </Link>

      <div className="ml-auto flex">
        <div className="cursor-pointer hover:underline flex items-center justify-center gap-2 px-5 border-r">
          <FaCartShopping color="#10B981" size={25} />
          <span>0 sản phẩm</span>
        </div>

        <div 
          className="cursor-pointer flex items-center justify-center px-5 gap-2 relative"
          onClick={()=>setIsShowOption(prev => !prev)}
          id="profile"
        >
          <FaUserCircle color="#10B981" size={25} />
          <span>Tài khoản</span>
          {isShowOption && <div onClick={e=>e.stopPropagation()} className="absolute flex flex-col top-full left-0 bg-gray-100 border min-w-[150px] py-2">
            <Link className="p-2 w-full hover:bg-sky-100" to={`/${path.MEMBER}/${path.PERSONAL}`} >Personal</Link>
            {+current.role === 1945 && <Link 
            className="p-2 w-full hover:bg-sky-100" to={`/${path.ADMIN}/${path.DASHBOARD}`} >Admin Workplace</Link>}
            <span className="p-2 w-full hover:bg-sky-100" onClick={()=>dispatch(logout())} >Logout</span>
          </div>}
        </div>
      </div>
    </div>
  );
};
export default Header;