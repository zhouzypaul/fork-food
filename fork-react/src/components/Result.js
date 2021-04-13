import TopBar from "./TopBar";
import {useEffect} from "react";
import Stars from "./Stars";
import Map from "./Map";

function Result(props) {
  const rest = props.location.resultProps.result;
  console.log(rest)

  const price = (p) => {
    let ret = "";
    for (let i = 0; i < p; i++) {
      ret += "$";
    }
    return ret;
  }

  // const location = {
  //   address: '',
  //   lat: rest.latitude,
  //   lng: rest.longitude
  // }

  return (
    <>
      <TopBar to="/home" showOptions={true}/>
      <div className="content">
        <div className="title-text">head to</div>
        <div className="restaurant" id="final-choice">
          <div className="title-text">{rest.name}</div>
          <div><Stars number={rest.numStars}/> from {rest.numReviews} reviews</div>
          <div>{price(rest.priceRange)} &#8226; {rest.city}, {rest.state}</div>
          {/*<Map location={location} zoom={10}/>*/}
        </div>
      </div>
    </>
  );
}

export default Result;