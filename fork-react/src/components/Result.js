import TopBar from "./TopBar";
import {useEffect} from "react";

function Result(props) {

  const getData = () => {

  }

  // useEffect(() => {
  //   const restaurant = props.location.resultProps.result;
  // })

  const restaurant = props.location.resultProps.result;

  return (
    <>
      <TopBar to="/home" showOptions={true}/>
      <div className="content">
        <div className="title-text">head to</div>
        <div>
          This is your restaurant selection: {restaurant}. Yeah its just an id but you could look it up in the database i guess.
        </div>
      </div>
    </>
  );
}

export default Result;