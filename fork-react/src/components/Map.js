import GoogleMapReact from 'google-map-react';
import LocationPin from "./LocationPin";

function Map(props) {
  return (
    <div className="map">
      <div className="google-map">
        <GoogleMapReact bootstrapURLKeys={{ key: '' }} defaultCenter={props.location} defaultZoom={props.zoom}>
          <LocationPin lat={props.location.lat} lng={props.location.lng}/>
        </GoogleMapReact>
      </div>
    </div>
  );
}

export default Map;