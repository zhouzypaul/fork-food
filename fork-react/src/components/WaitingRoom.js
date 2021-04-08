import TopBar from "./TopBar";

function WaitingRoom() {
    // values same as room code
    return (
        <>
            <TopBar showOptions={true} to="/home" />
            <div className="content">
                hi
            </div>
        </>
    );
}

export default WaitingRoom;