import React, { useState, useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, useMapEvents, useMap } from 'react-leaflet';
import { Input } from '../components/ui/input';
import { Button } from '../components/ui/button';
import { Label } from '../components/ui/label';
import L from 'leaflet';
import { MapPin, Search, Loader2 } from 'lucide-react';
import 'leaflet/dist/leaflet.css';

delete (L.Icon.Default.prototype as { _getIconUrl?: unknown })._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

interface AddressInputProps {
  address: string;
  latitude: number;
  longitude: number;
  onAddressChange: (address: string) => void;
  onCoordinatesChange: (lat: number, lng: number) => void;
}

const MapClickHandler: React.FC<{
  latitude: number;
  longitude: number;
  onCoordinatesChange: (lat: number, lng: number) => void;
  onAddressFromCoords: (lat: number, lng: number) => void;
}> = ({ latitude, longitude, onCoordinatesChange, onAddressFromCoords }) => {
  const map = useMap();
  const [markerPosition, setMarkerPosition] = useState<[number, number]>([latitude, longitude]);
  
  useMapEvents({
    click: async (e) => {
      const { lat, lng } = e.latlng;
      setMarkerPosition([lat, lng]);
      onCoordinatesChange(lat, lng);
      await onAddressFromCoords(lat, lng);
    },
  });

  useEffect(() => {
    setMarkerPosition([latitude, longitude]);
  }, [latitude, longitude]);

  return markerPosition[0] !== 0 ? (
    <Marker position={markerPosition} />
  ) : null;
};

const MapUpdater: React.FC<{ latitude: number; longitude: number }> = ({ latitude, longitude }) => {
  const map = useMap();
  
  useEffect(() => {
    if (latitude && longitude) {
      map.setView([latitude, longitude], map.getZoom());
    }
  }, [latitude, longitude, map]);
  
  return null;
};

export const AddressInput: React.FC<AddressInputProps> = ({
  address,
  latitude,
  longitude,
  onAddressChange,
  onCoordinatesChange,
}) => {
  const [isSearching, setIsSearching] = useState(false);
  const [mapKey, setMapKey] = useState(0);
  const searchTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const handleAddressSearch = async () => {
    if (!address.trim()) return;
    
    setIsSearching(true);
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}&limit=1`,
        {
          headers: {
            'User-Agent': 'Tramplin/1.0',
          },
        }
      );
      const data = await response.json();
      
      if (data && data.length > 0) {
        const result = data[0];
        const lat = parseFloat(result.lat);
        const lon = parseFloat(result.lon);
        onCoordinatesChange(lat, lon);
        
        const addr = result.address;
        const parts: string[] = [];
        
        if (addr.country) parts.push(addr.country);
        if (addr.city || addr.town || addr.village || addr.municipality) {
          parts.push(addr.city || addr.town || addr.village || addr.municipality);
        }
        if (addr.road) {
          let road = addr.road;
          if (addr.house_number) road += `, ${addr.house_number}`;
          parts.push(road);
        }
        
        const shortAddress = parts.join(', ');
        onAddressChange(shortAddress || result.display_name);
        setMapKey((k) => k + 1);
      } else {
        console.log('Адрес не найден');
      }
    } catch (error) {
      console.error('Ошибка геокодирования:', error);
    } finally {
      setIsSearching(false);
    }
  };

  const handleAddressFromCoords = async (lat: number, lng: number) => {
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`,
        {
          headers: {
            'User-Agent': 'Tramplin/1.0',
          },
        }
      );
      const data = await response.json();
      
      if (data && data.address) {
        const addr = data.address;
        const parts: string[] = [];
        
        if (addr.country) parts.push(addr.country);
        if (addr.city || addr.town || addr.village || addr.municipality) {
          parts.push(addr.city || addr.town || addr.village || addr.municipality);
        }
        if (addr.road) {
          let road = addr.road;
          if (addr.house_number) road += `, ${addr.house_number}`;
          parts.push(road);
        }
        
        const shortAddress = parts.join(', ');
        onAddressChange(shortAddress || data.display_name);
      }
    } catch (error) {
      console.error('Ошибка обратного геокодирования:', error);
    }
  };

  const handleAddressInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    onAddressChange(value);
    
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleAddressSearch();
    }
  };

  const defaultCenter: [number, number] = [latitude || 55.751244, longitude || 37.618423];

  return (
    <div className="space-y-2">

      <div className="flex gap-2">
        <Input
          value={address}
          onChange={handleAddressInputChange}
          onKeyDown={handleKeyDown}
          placeholder="Введите адрес или кликните на карту"
          className="flex-1"
        />
        <Button
          type="button"
          variant="secondary"
          onClick={handleAddressSearch}
          disabled={isSearching || !address.trim()}
        >
          {isSearching ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
        </Button>
      </div>
      <p className="text-xs text-gray-500">Введите адрес и нажмите Enter или кнопку поиска, либо кликните на карту</p>
      
      <div className="h-[300px] rounded-lg overflow-hidden border">
        <MapContainer
          center={defaultCenter}
          zoom={12}
          style={{ height: '100%', width: '100%' }}
          key={mapKey}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <MapUpdater latitude={latitude} longitude={longitude} />
          <MapClickHandler
            latitude={latitude}
            longitude={longitude}
            onCoordinatesChange={onCoordinatesChange}
            onAddressFromCoords={handleAddressFromCoords}
          />
        </MapContainer>
      </div>
      <div className="flex gap-4 text-xs text-gray-500">
        <span>Координаты: {latitude.toFixed(6)}, {longitude.toFixed(6)}</span>
      </div>
    </div>
  );
};
