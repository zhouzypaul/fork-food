import { Icon } from '@iconify/react';
import locationIcon from '@iconify/icons-mdi/map-marker';

function LocationPin() {
  return (
    <div className="pin">
      <Icon icon={locationIcon} className="pin-icon"/>
    </div>
  );
}

export default LocationPin;