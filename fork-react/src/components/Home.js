import TopBar from "./TopBar";

function Home() {
  return (
    <>
      <TopBar to='/home' showOptions={true}/>
      <div className="content">
        <button>
          Host
        </button>
        <br/>
        <button>
          Join
        </button>
      </div>
    </>
  );
}

export default Home;