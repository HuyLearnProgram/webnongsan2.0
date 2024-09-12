import React from 'react'
import {
    Banner,
    Sidebar,
} from "../../components";
const Home = () => {
    return (
        <>
            <div className="w-main flex mt-6">
                <div className="flex flex-col gap-5 w-[25%] flex-auto">
                    <Sidebar />
                </div>
                <div className="flex flex-col gap-5 pl-5 w-[75%] flex-auto">
                    <Banner />
                </div>
            </div>
        </>

    )
}

export default Home