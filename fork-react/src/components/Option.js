import Stars from "./Stars";

function Option(props) {
  return (
    <div className="restaurant">
      <div className="title-text">{props.name}</div>
      <div className="rest-info">
        <div><Stars number={props.star}/> &#183; {props.review} reviews</div>
        <div>{props.price} &#183; {props.distance}</div>
      </div>
    </div>
  );
}

export default Option;